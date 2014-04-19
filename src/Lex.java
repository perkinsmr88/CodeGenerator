import java.io.FileNotFoundException;
import java.io.File;
import java.lang.*;
import java.util.*;
import java.util.regex.*;


public class Lex
{
    String[] sym  = {"int","if","else","while","void","return","float"}; //Symbol Table
    String[] dLim = {"+","-","*","/","<",">","=",";",",","(",")","{","}","\r","\n","\t","[","]"}; //Special Character and Delimiter Table
    boolean comCheck = false; //comment flag
    boolean notDim = true; //delimiter flag
    String current = ""; //character being examined
    String analyzeMe = ""; //word in progress/being analyzed
    char primtest = ' ';
    char SecTest = ' ';
    int multi = 0;
    int depth = 0;
    int lineNum = 0;
    ArrayDeque<imAtoken> stacker = new ArrayDeque<imAtoken>();

    public ArrayDeque<imAtoken> run(String[] args)
    {



        try
        {
            //grab the file to be compiled
            File file = new File("C:\\Users\\Drivenwanderer\\Documents\\GitHub\\CodeGenerator\\Test Files\\cg1");
            //File file = new File(args[0]);

            Scanner scanner =  new Scanner(file); //File Scanner

            while (scanner.hasNextLine()) //until we reach the end of the file...
            {
                lineNum++; //Increment Line Number

                String Test = scanner.nextLine(); //Scan selected Line in

                for(int i = 0; i < Test.length(); i++)
                {
                    notDim = false; //reset Delimiter flag

                    if((i+1) < Test.length())
                    {
                        primtest = Test.charAt(i);
                        SecTest  = Test.charAt(i+1);
                    }
                    else
                    {
                        primtest = Test.charAt(i);
                        SecTest  = ' ';
                    }

                    if(primtest == '/' && SecTest == '/' && Test.charAt(i+2) != '*')
                    {
                        break;
                    }

                    if(primtest == '/' && SecTest == '*')
                    {
                        multi++;
                        comCheck = true;
                        SecTest = ' ';
                        i++;
                        continue;
                    }

                    if(primtest == '*' && SecTest == '/' && comCheck && multi != 0)
                    {
                        multi--;

                        if(multi == 0)
                        {
                            comCheck = false;
                        }

                        SecTest = ' ';
                        i++;
                        continue;
                    }

                    if(!comCheck)
                    {
                        notDim = true;

                        for (String aDLim : dLim)
                        {
                            current = String.valueOf(primtest);

                            if (current.equals("+") || current.equals("-"))
                            {
                                notDim = String.valueOf(Test.charAt(i - 1)).equals("E");

                                if (String.valueOf(SecTest).equals(current)) {
                                    notDim = false;
                                    current = current + SecTest;

                                }

                            } else {

                                if (current.equals(aDLim) || current.equals(" "))
                                {
                                    notDim = false;

                                    if (current.equals("{"))
                                    {
                                        depth++;
                                    }

                                    if (current.equals("}"))
                                    {
                                        depth--;
                                    }
                                }
                            }
                        }

                        if(notDim && i < Test.length())
                        {
                            analyzeMe += primtest;
                        }
                        else
                        {
                            analyzeMe = keyHunt(analyzeMe, sym);
                            analyzeMe = intHunt(analyzeMe, dLim);

                            if(!analyzeMe.equals(""))
                            {
                               analyzeMe = "";
                            }

                            if((!current.equals("")) && (!current.equals(" ") ))
                            {
                                if(current.equals("="))
                                {
                                    if(String.valueOf(SecTest).equals(current))
                                    {
                                        current += SecTest;
                                        i++;
                                    }
                                }

                                if(current.equals("!") && String.valueOf(SecTest).equals("="))
                                {
                                    current += SecTest;
                                    i++;
                                }

                                if((current.equals(">") || current.equals("<")) && String.valueOf(SecTest).equals("="))
                                {
                                    current += SecTest;
                                    i++;
                                }

                                if(!(current.equals("\t") || current.equals("\r") || current.equals("\n")))
                                {
                                    imAtoken token = new imAtoken("Symbol", current, depth,lineNum);
                                    stacker.add(token);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) // if the file can not be scanned in....
        {
            e.printStackTrace(); //show me where it fails
        }

        if(!analyzeMe.equals(""))
        {
            analyzeMe = keyHunt(analyzeMe, sym);
            analyzeMe = intHunt(analyzeMe, dLim);
        }

        return stacker;
    }

    String keyHunt(String analyzeMe, String[] sym)
    {
        boolean keyCheck = false;

        for (String aSym : sym) {
            if (analyzeMe.equals(aSym)) {
                imAtoken token = new imAtoken("Keyword", analyzeMe, depth,lineNum);
                stacker.add(token);
                analyzeMe = "";
                keyCheck = true;
            }
        }

        if(!keyCheck && !analyzeMe.equals(""))
        {
            analyzeMe = idHunt(analyzeMe);
        }
        return analyzeMe;
    }

    String idHunt(String analyzeMe)
    {
        boolean idCheck = true;

        for(int r = 0; r < analyzeMe.length(); r++)
        {
            if(!Character.isLetter(analyzeMe.charAt(r)))
            {
                idCheck = false;
            }
        }

        if(idCheck)
        {
            imAtoken token = new imAtoken("ID", analyzeMe, depth,lineNum);
            stacker.add(token);
            analyzeMe = "";
        }

        return analyzeMe;
    }

    String intHunt(String analyzeMe, String[] dLim)
    {
        boolean intCheck = true;
        boolean errorCheck = false;

               for(int r = 0; r < analyzeMe.length(); r++)
        {
            for (String aDLim : dLim) {
                String wall = String.valueOf(analyzeMe.charAt(r));
                char egg = analyzeMe.charAt(r);


                if (!wall.equals(aDLim) && !Character.isLetterOrDigit(egg)) {
                    errorCheck = true;
                }

                if (!Character.isDigit(analyzeMe.charAt(r))) {
                    intCheck = false;
                }
            }
        }

        if(errorCheck)
        {
            analyzeMe = checkFloat(analyzeMe);
        }

        if(intCheck && !analyzeMe.equals(""))
        {
            imAtoken token = new imAtoken("int", analyzeMe, depth, lineNum);
            stacker.add(token);
            analyzeMe = "";
        }

        return analyzeMe;
    }

   String checkFloat(String analyzeMe)
    {
        int counter = 0;

        //Evaluate if the word is a float.
        String regex = "\\d*\\.?\\d+([E][+-]?\\d+)?";
        Pattern floatPat = Pattern.compile(regex); //create a pattern to match
        Matcher match = floatPat.matcher(analyzeMe); //look for a match
        boolean isFloat = match.matches(); //Did it match?

        if(isFloat) {
            //Success, output it.
            imAtoken token = new imAtoken("float", analyzeMe, depth,lineNum);
            stacker.add(token);
            analyzeMe = "";
        }
        else
        {
            //Check to see if there were too many decimals.
            for(int i = 0; i < analyzeMe.length(); i++)
            {
                if(analyzeMe.charAt(i) == '.')
                {
                    counter++;
                }
            }

            //If there is more than 1 decimal, output an error.
            if(counter > 1)
            {
               System.out.println("ERROR: invalid token \"" + analyzeMe + "\" found on line " + lineNum);
                analyzeMe = "";
            }
        }
        return analyzeMe;
    }


}
