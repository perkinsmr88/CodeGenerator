import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: Michael Perkins
 * Date: 1/12/14
 * Time: 6:37 PM
 */

public class Compiler
{
    public static void main(String[] args)
    {
        ArrayDeque<imAtoken> parseMe = new ArrayDeque<imAtoken>();// collection of analyzed pieces to be parsed
        String conti   = ""; //Can it be parsed?
        int[] x = new int[10];

        try {

            Lex lex = new Lex(); //Lexical Analyzer creation
            parseMe = lex.run(args); //Create the string from the lexical analyzer

            //Call the Parser------------------------------------------------

            /*Parser parser = new Parser();  //Parser creation
            conti = parser.run(parseMe, functionList, conti);   //run through parser*/

            //Call the Code Generator----------------------------------------
            CodeGen codeGen = new CodeGen();
            conti = codeGen.run(parseMe);

            //Print Successful Result-----------------------------------------

            System.out.println(conti);
        }
        catch(Exception e)
        {
            System.out.println("Somethings wrong with the test file, chief\n");
           e.printStackTrace();
        }
    }
}
