import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by Michael Perkins.
 */
public class CodeGen
{
    //Quad table builders
    ArrayList<String> operator = new ArrayList<String>();
    ArrayList<String> operand1 = new ArrayList<String>();
    ArrayList<String> operand2 = new ArrayList<String>();
    ArrayList<String> result   = new ArrayList<String>();

    String tempID; //for holding IDs over through function calls
    String prevAssn; //variable for assignment
    String gen; //temp variable
    int genCount = 1; //number of temp variable
    String currentFunc;
    String funcType;
    String prevcalcs;
    int paraCounter = 0;
    String num = "";
    String rel = "";

//---------------------------------------------------------------------------------------------------------------

    public String run(ArrayDeque<imAtoken> x)
    {
        try
        {
            while (!x.isEmpty())
            {
                program(x);
            }
        }
        catch(Exception e)
        {
            //report bad test file
            System.out.println("Bad Code, Bro\n");

            //Print error message
            System.out.println(e);

            //end the program cleanly
            System.exit(0);
        }

        //Print out generated code
        printCode();

        //End Message
        return "Code Successfully Generated";
    }

//---------------------------------------------------------------------------------------------------------------

    public void program(ArrayDeque<imAtoken> x)
    {
        declist(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declist(ArrayDeque<imAtoken> x)
    {
        declaration(x);
        decloop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void declaration(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            typeSpec(x);

            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                //keep track of current function
                currentFunc = token.name;

                //Remove the token
                x.pop();
            }
            else
            {
                System.out.println("Error1: expected an ID but got a " + token.type + " on line "  + token.line);
                System.exit(0);
            }

            decFollow(x);
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void decloop(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
            {
                declaration(x);
                decloop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void typeSpec(ArrayDeque<imAtoken> x)
    {
        imAtoken token = x.peek();

        if( token.name.equals("int") || token.name.equals("void") || token.name.equals("float") )
        {
            //capture function type
            funcType = token.name;

            //Remove the token from the stack
            x.pop();
        }
        else
        {
            System.out.println("Error2: expected an int, void, or float but found " + token.name + " on line "  + token.line);
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void decFollow(ArrayDeque<imAtoken> x)
    {
        //decfollow -> (params) compound | X

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {

                //add FUNC to the operator List
                operator.add("FUNC");

                //add the functions name to operand1
                operand1.add(currentFunc);

                //operand2 is the functions return type
                operand2.add(funcType);

                x.pop();

                //clone current stack to...
                ArrayDeque<imAtoken> y = x.clone();

                //find parameters without destroying everything
                params2(y);

                //Count the number of parameters
                result.add(Integer.toString(paraCounter));

                //reset ParaCounter
                paraCounter = 0;

                params(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        //remove end token
                        x.pop();

                        compound(x);
                    }
                }
            }
            else if(token.name.equals(";") || token.name.equals("["))
            {
                X(x);
            }
            else
            {
                System.out.println("Error3: Expected \"(\" or \";\" or \"[\" but found " + token + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void params(ArrayDeque<imAtoken> x)
    {
        //params-> int ID paramtype parLoop | float ID paramtype parLoop | void parameter

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("float"))
            {
                //remove the token
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.type.equals("ID"))
                    {
                       //remove the token
                        x.pop();

                        //add Param to the operator List
                        operator.add("PARAM");

                        //operands are currently empty
                        operand1.add("");
                        operand2.add("");

                        //the result is currently empty
                        result.add("");

                        //add a parameter allocation to the operator list
                        operator.add("ALLOC");

                        //operand 1 is the size allocation for an int or float
                        operand1.add("4");

                        //operand 2 is not used here
                        operand2.add("");

                        //set tempID equal to current token
                        tempID = token.name;

                        //add the ID to the result list
                        result.add(tempID);

                        paramType(x);

                        parLoop(x);
                    }
                }
                else
                {
                    System.out.println("Error: Out of Tokens");
                    System.exit(0);
                }
            }
            else
            {
                if(token.name.equals("void"))
                {
                    //remove token
                    x.pop();

                    parameter(x);
                }
                else
                {
                    System.out.println("Error4: Expected int, float, or void but found " + token + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void params2(ArrayDeque<imAtoken> y)
    {
        //params-> int ID paramtype parLoop | float ID paramtype parLoop | void parameter

        if(!y.isEmpty())
        {
            imAtoken token = y.peek();

            if(token.name.equals("int") || token.name.equals("float"))
            {
                //remove the token
                y.pop();

                if(!y.isEmpty())
                {
                    token = y.peek();

                    if(token.type.equals("ID"))
                    {
                        //increment parameter counter
                        paraCounter++;

                        //remove the token
                        y.pop();

                        paramType(y);

                        parLoop(y);
                    }
                }
                else
                {
                    System.out.println("Error: Out of Tokens");
                    System.exit(0);
                }
            }
            else
            {
                if(token.name.equals("void"))
                {
                    //remove token
                    y.pop();

                    parameter(y);
                }
                else
                {
                    System.out.println("Error4: Expected int, float, or void but found " + token + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void compound(ArrayDeque<imAtoken> x)
    {
        //compound-> { localDecs statementList }
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{"))
            {
                x.pop();

                localDecs(x);

                statementList(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("}"))
                    {
                        operator.add("END");
                        operand1.add("FUNC");
                        operand2.add(currentFunc);
                        result.add("");
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error5: expected \"}\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error: Out of Tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error6: expected \"{\" but found " + token.name + " on line " + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void X(ArrayDeque<imAtoken> x)
    {
        //X-> ; | [NUM] ;

        if(!x.isEmpty())
        {
            //add an allocation to the operator list
            operator.add("ALLOC");

            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                //allocate 4 bits for the new variable
                operand1.add("4");

                //operand 2 is not used here
                operand2.add("");

                //add the current tempID to the result list
                result.add(tempID);

                //remove the current token
                x.pop();
            }
            else
            {
                if(token.name.equals("["))
                {
                    x.pop();

                    NUM(x);

                    token = x.peek();

                    if(token.name.equals("]"))
                    {
                        //operand one holds the allocation size for the array
                        operand1.add(Integer.toString(Integer.parseInt(num) * 4));

                        //operand 2 isnt used here
                        operand2.add("");

                        //add the tempId to the result list
                        result.add(tempID);


                        x.pop();

                        token = x.peek();

                        if(token.name.equals(";"))
                        {
                            x.pop();
                        }
                        else
                        {
                            System.out.println("Error7: expected \";\" but found " + token.name + " on line "  + token.line);
                            System.exit(0);
                        }
                    }
                    else
                    {
                        System.out.println("Error8: expected \"]\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error9: expected \"[\" but found " + token.name + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
        else
        {
            System.out.println("Error: out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void NUM(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("float") ||  token.type.equals("int"))
            {
                //calculate and store the allocation size for the array
                num = token.name;

                //remove the current token
                x.pop();
            }
            else
            {
                System.out.println("Error10: expected an Int or a Float but found a " + token.type + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error: Out of Tokens");
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void localDecs(ArrayDeque<imAtoken> x)
    {
        //localDecs-> typeSpec ID X localDecs | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("int") || token.name.equals("void") || token.name.equals("float"))
            {
                typeSpec(x);

                token = x.peek();

                if(token.type.equals( "ID"))
                {
                    tempID = token.name;

                    x.pop();

                    X(x);

                    localDecs(x);
                }
                else
                {
                    System.out.println("Error: Expected an ID found " + token + " on line "  + token.line);
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void statementList(ArrayDeque<imAtoken> x)
    {
        //statementList-> statement statementList | empty
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("{") || token.name.equals("(") || token.name.equals("if") || token.name.equals("while") || token.name.equals("return") || token.type.equals("ID") || token.type.equals("Int") || token.type.equals("Float") || token.name.equals(";"))
            {
                statement(x);

                statementList(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void statement(ArrayDeque<imAtoken> x)
    {
        //statement-> expressionSt | compound | selectionSt | iterationSt | returns
        // ( || { || if || while || return

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("Int") || token.type.equals("Float") || token.type.equals("ID") || token.type.equals(";"))
            {
                expressionSt(x);
            }
            else if (token.name.equals("{"))
            {
                compound(x);
            }
            else if (token.name.equals("if"))
            {
                selectionSt(x);
            }
            else if (token.name.equals("while"))
            {
                iterationSt(x);
            }
            else if (token.name.equals("return"))
            {
                returnSt(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void param(ArrayDeque<imAtoken> x)
    {
        // param-> typeSpec ID paramType

        typeSpec(x);

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                //set tempID equal to the current token
                tempID = token.name;

                //add a parameter allocation to the operator list
                operator.add("ALLOC");

                //operand1 holds the allocation size for a single int or float
                operand1.add("4");

                //operand2 isnt used here
                operand2.add("");

                //add the current tempID to the results list
                result.add(tempID);

                x.pop();

                paramType(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void paramType(ArrayDeque<imAtoken> x)
    {
        //paramType-> [ ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {
                //remove token
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("]"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error11: expected \"]\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.print("Error: Out of Tokens");
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void parLoop(ArrayDeque<imAtoken> x)
    {
        //parLoop-> , param parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                param(x);

                parLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void parameter(ArrayDeque<imAtoken> x)
    {
        //parameter-> ID paramtype parLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.type.equals("ID"))
            {
                x.pop();

                paramType(x);

                parLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expressionSt(ArrayDeque<imAtoken> x)
    {
        //expressionSt-> expression ; | ;

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(";"))
            {
                x.pop();
            }
            else
            {
                //arraylist for storing tokens in use
                ArrayList<imAtoken> expTokens = new ArrayList<imAtoken>();

                //clone the arraydeque
                ArrayDeque<imAtoken> y = x.clone();

                while(!y.peek().name.equals(";"))
                {
                    //pop the tokens out of the arraydeque and put it into the array list
                    expTokens.add(y.pop());
                }

                //Generate Code.
                expresso(expTokens);

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(";"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error12: expected \";\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error13: Out of Tokens");
                    System.exit(0);
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void selectionSt(ArrayDeque<imAtoken> x)
    {
        //selectionSt-> if ( expression ) statement selectFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("if"))
            {
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();

                        //Code Gen
                        int count = 0;

                        ArrayList<imAtoken> expToks = new ArrayList<imAtoken>();

                        ArrayDeque<imAtoken> y = x.clone();

                        while(!y.peek().name.equals(")"))
                        {
                            expToks.add(y.pop());
                            count++;
                        }

                        //Generate Code.
                        expresso(expToks);

                        //set relative operator
                        operator.add(relopSym(rel));

                        //clear relative operator
                        rel = "";

                        operand1.add(result.get(result.size()-1));
                        operand2.add("");
                        result.add(Integer.toString(result.size()+3));


                        int ifbackpatch = result.size();

                        //add if branch line
                        operator.add("BR");
                        operand1.add("");
                        operand2.add("");
                        result.add(" ");
                        
                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                statement(x);

                                int elsepatch = 0;

                                boolean hasElse = false;

                                if(y.peek().name.equals("else"))
                                {
                                    result.add(ifbackpatch, Integer.toString(result.size()+2));
                                    result.remove(ifbackpatch+1);

                                    hasElse = true;

                                    ///add else branch line
                                    operator.add("BR");
                                    operand1.add("");
                                    operand2.add("");
                                    result.add("else");

                                    elsepatch = result.size();
                                }
                                else
                                {
                                    result.add(ifbackpatch, Integer.toString(result.size()+1));
                                    result.remove(ifbackpatch+1);
                                }

                                selectFollow(x);

                                if(hasElse)
                                {
                                    result.add(elsepatch-1, Integer.toString(result.size()+1));
                                    result.remove(elsepatch);
                                }
                            }
                            else
                            {
                                System.out.println("Error14: expected \")\" but got " + token.name + " on line "  + token.line);
                                System.exit(0);
                            }
                        }
                        else
                        {
                            System.out.println("Error15: out of tokens");
                            System.exit(0);
                        }
                    }
                }
                else
                {
                    System.out.println("Error16: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error17: expected \"if\" but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error18: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void iterationSt(ArrayDeque<imAtoken> x)
    {
        //iterationSt-> while ( expression ) statement

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("while"))
            {
                x.pop();

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("("))
                    {
                        x.pop();
                        //Code Gen
                        int count = 0;
                        ArrayList<imAtoken> expToks = new ArrayList<imAtoken>();

                        ArrayDeque<imAtoken> y = x.clone();

                        while(!y.peek().name.equals(")"))
                        {
                            expToks.add(y.pop());
                            count++;

                        }

                        //Generate Code.
                        int start = result.size()+1;
                        expresso(expToks);

                        operator.add(relopSym(rel));
                        rel = "";
                        operand1.add(result.get(result.size()-1));
                        operand2.add("");
                        result.add("???");
                        int whilebackpatch = result.size()-1;


                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                operator.add("BLOCK");
                                operand1.add("");
                                operand2.add("");
                                result.add("");

                                statement(x);

                                operator.add("END");
                                operand1.add("BLOCK");
                                operand2.add("");
                                result.add("");

                                operator.add("BR");
                                operand1.add("");
                                operand2.add("");
                                result.add(Integer.toString(start));

                                result.add(whilebackpatch, Integer.toString(result.size()+1));
                                result.remove(whilebackpatch+1);
                            }
                            else
                            {
                                System.out.println("Error19: expected \")\" but found " + token.name + " on line "  + token.line);
                                System.exit(0);
                            }
                        }
                    }
                    else
                    {
                        System.out.println("Error20: expected \"(\" but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error21: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                System.out.println("Error22: expected \"while\" but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error23: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void returnSt(ArrayDeque<imAtoken> x)
    {
        //returnSt-> return retFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("return"))
            {
                x.pop();

                retFollow(x);
            }
            else
            {
                System.out.println("Error24: expected return but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
        else
        {
            System.out.println("Error25: out of tokens");
            System.exit(0);
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expression(ArrayDeque<imAtoken> x)
    {
        //expression-> ( expression ) expFollow | NUM expFollow | ID idFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();

                        expFollow(x);
                    }
                    else
                    {
                        System.out.println("Error26: expected ) but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error27: out of tokens");
                    System.exit(0);
                }
            }
            else if(token.type.equals("int") || token.type.equals("float"))
            {
                NUM(x);

                expFollow(x);
            }
            else if(token.type.equals("ID"))
            {
                //keep track of this ID for later
               prevAssn = token.name;

                //set operand1 to the current token
               //operand1.add(token.name);

                x.pop();

                idFollow(x);
            }
            else
            {
                System.out.println("Error: expected stuff but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void selectFollow(ArrayDeque<imAtoken> x)
    {
        //selectFollow-> else statement | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("else"))
            {
                x.pop();

                statement(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void retFollow(ArrayDeque<imAtoken> x)
    {
        //retFollow-> ; | expression ;
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            //return on a void statement
            if(token.name.equals(";"))
            {
                //return function found, add to operator list
                operator.add("RETURN");
                operand1.add("");
                operand2.add("");
                result.add("");

                x.pop();
            }
            else
            {
                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(";"))
                    {
                        //return function found, add to operator list
                        operator.add("RETURN");
                        operand1.add("");
                        operand2.add("");
                        result.add(prevcalcs);

                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error28: expected ; but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void expFollow(ArrayDeque<imAtoken> x)
    {
        //expFollow-> termloop addExpLoop C

        termLoop(x);

        addExpLoop(x);

        C(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void termLoop(ArrayDeque<imAtoken> x)
    {
        //termLoop-> mulop factor termLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("*") || token.name.equals("/"))
            {
                //take note of the symbol used here for later use
                String op = token.name;

                mulop(x);

                factor(x);

                termLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void C(ArrayDeque<imAtoken> x)
    {
        //C-> relop addExp | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("<=") || token.name.equals("<") || token.name.equals(">") || token.name.equals(">=") || token.name.equals("==") || token.name.equals("!="))
            {
                relop(x);

                addExp(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void relop(ArrayDeque<imAtoken> x)
    {
        //relop->  <= | <  | > | >= | == | !=

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("<=") || token.name.equals("<") || token.name.equals(">") || token.name.equals(">=") || token.name.equals("==") || token.name.equals("!="))
            {
                x.pop();
            }
            else
            {
                System.out.println("Error29: expected ; but found " + token.name + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void addExp(ArrayDeque<imAtoken> x)
    {
        //addExp-> term addExpLoop

        term(x);

        addExpLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void term(ArrayDeque<imAtoken> x)
    {
        factor(x);

        termLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void addExpLoop(ArrayDeque<imAtoken> x)
    {
        //addExpLoop-> addop term addExpLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("+") || token.name.equals("-"))
            {
                x.pop();

                term(x);

                addExpLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void mulop(ArrayDeque<imAtoken> x)
    {
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("*") || token.name.equals("/"))
            {
                x.pop();
            }
            else
            {
                System.out.println("Error: looking for * or / and found " + token + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void factor(ArrayDeque<imAtoken> x)
    {
        //factor-> (expression) | NUM | ID factorFollow
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error30: expected ; but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error31: out of tokens");
                    System.exit(0);
                }
            }
            else if(token.type.equals("int") || token.type.equals("float"))
            {
                NUM(x);
            }
            else if(token.type.equals("ID"))
            {
                //store the current token as operand 2
                operand2.add(token.name);

                //remove the current token
                x.pop();

                factorFollow(x);
            }
            else
            {
                System.out.println("Error32: expected ( or an int or a float or an ID but found " + token.type + " on line "  + token.line);
                System.exit(0);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void factorFollow(ArrayDeque<imAtoken> x)
    {
        //factorFollow-> B | ( args)
        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                args(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();
                    }
                }
            }
            else
            {
                B(x);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void idFollow(ArrayDeque<imAtoken> x)
    {
        //idFollow-> BM | ( args ) expFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("("))
            {
                x.pop();

                args(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals(")"))
                    {
                        x.pop();

                        expFollow(x);
                    }
                    else
                    {
                        System.out.println("Error33: expected ) but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error34: out of tokens");
                    System.exit(0);
                }
            }
            else
            {
                B(x);

                M(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void args(ArrayDeque<imAtoken> x)
    {
        //args-> argList | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if (token.name.equals("(") || token.type.equals("int") || token.type.equals("float") || token.type.equals("ID"))
            {
                argList(x);
            }

        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void argList(ArrayDeque<imAtoken> x)
    {
        //argList-> expression argListLoop

        expression(x);

        argsListLoop(x);
    }

//---------------------------------------------------------------------------------------------------------------

    public void argsListLoop(ArrayDeque<imAtoken> x)
    {
        //argListLoop-> , expression argListLoop | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals(","))
            {
                x.pop();

                expression(x);

                argsListLoop(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public void B(ArrayDeque<imAtoken> x)
    {
        //B-> [ expression ] | empty

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("["))
            {
                x.pop();

                expression(x);

                if(!x.isEmpty())
                {
                    token = x.peek();

                    if(token.name.equals("]"))
                    {


                        x.pop();
                    }
                    else
                    {
                        System.out.println("Error35: expected ] but found " + token.name + " on line "  + token.line);
                        System.exit(0);
                    }
                }
                else
                {
                    System.out.println("Error36: out of tokens");
                    System.exit(0);
                }
            }
        }

    }

//---------------------------------------------------------------------------------------------------------------

    public void M(ArrayDeque<imAtoken> x)
    {
        //M-> = expression | expFollow

        if(!x.isEmpty())
        {
            imAtoken token = x.peek();

            if(token.name.equals("="))
            {
                x.pop();

                expression(x);
            }
            else
            {
                expFollow(x);
            }
        }
    }

//---------------------------------------------------------------------------------------------------------------

    public String makeTemp()
    {
        //create a temp variable with an iterated number
        gen = "t" + genCount;

        //increment the variable number for next time
        genCount++;

        //return the variable for use
        return gen;
    }

//---------------------------------------------------------------------------------------------------------------

    void printCode()
    {
        //create the display headers
        System.out.println("#    Operator       Op1           Op2          Result");
        System.out.println("------------------------------------------------------");

        //print out the generated code
        for(int i = 0; i < operator.size(); i++)
        {
            //print the formatted columns from each list under their respective headers
            System.out.println(String.format("%-3s  %-7s        %-4s          %-4s          %-5s", (i + 1), operator.get(i), operand1.get(i), operand2.get(i), result.get(i)));
        }

        //Close the generated Code
        System.out.println("------------------------------------------------------");
    }


    public ArrayList<imAtoken> handleAddSub(ArrayList<imAtoken> y) 
    {
        int start = 0;
        boolean search = true;

        while (search) 
        {
            String symbol = "";

            for (int i = 0; i < y.size(); i++) 
            {
                if (y.get(i).name.equals("+") || y.get(i).name.equals("-")) 
                {
                    start = i;
                    symbol = y.get(i).name;
                }
            }

            //if neither +/- is found then there is nothing more to be done here
            if (start == 0) 
            {
                //break the while loop and leave
                search = false;
                break;
            }

            //Assign operator accordingly
            if (symbol.equals("+")) 
            {
                operator.add("ADD");
            }
            else if (symbol.equals("-"))
            {
                operator.add("SUB");
            }

            //Assign the operands and result
            operand1.add(y.get(start - 1).name);
            operand2.add(y.get(start + 1).name);
            result.add(makeTemp());



            imAtoken replace = new imAtoken(gen);

            //add the temporary token to the list
            y.add(start - 1, replace);
           
            for (int j = 0; j < 3; j++) 
            {
                y.remove(start);
            }

            //reset the startloc
            start = 0;
        }

        return y;
    }

    public ArrayList<imAtoken> handleMultDiv(ArrayList<imAtoken> y)
    {
        int start = 0;
        boolean search = true;

        while(search)
        {
            String symbol = "";
            for(int i = 0; i < y.size(); i++)
            {
                if (y.get(i).name.equals("*") || y.get(i).name.equals("/"))
                {
                    start = i;
                    symbol = y.get(i).name;
                }
            }

            if(start == 0)
            {
                search = false;
                break;
            }

            if(symbol.equals("*"))
            {
                operator.add("MUL");
            }
            else if(symbol.equals("/"))
            {
                operator.add("DIV");
            }

            operand1.add(y.get(start - 1).name);
            operand2.add(y.get(start + 1).name);
            result.add(makeTemp());

            imAtoken replace = new imAtoken(gen);

            y.add(start-1, replace);

            for(int j = 0; j < 3; j++)
            {
                y.remove(start);
            }

            start = 0;
        }

        return y;
    }

    public ArrayList<imAtoken> handleParens(ArrayList<imAtoken> y)
    {
        boolean keepsearching = true;
        int depth = 0;
        int start = 0;
        int endingLoc = 0;


        while(keepsearching)
        {
            for (int i = 0; i < y.size(); i++)
            {
                if (y.get(i).name.equals("("))
                {
                    depth++;
                    start = i;
                }
            }
            if (depth == 0)
                keepsearching = false;
            if (depth != 0)
            {
                for (int k = start; k < y.size(); k++)
                {
                    if (y.get(k).name.equals(")"))
                    {
                        endingLoc = k;
                    }
                }
            }

            ArrayList<imAtoken> working = new ArrayList<imAtoken>();

            for (int i = 1; i < (endingLoc - start); i++)
            {
                working.add(y.get(start + i));
            }

            if (working.size() > 0)
            {
                working = handleMultDiv(working);
                working = handleAddSub(working);

                y.add(start, working.get(0));

                for(int i = endingLoc + 1; i > start; i--)
                {
                    y.remove(i);
                }
            }
            //reset locations and depths
            endingLoc = 0;
            start = 0;
            depth = 0;
        }

        return y;
    }

    public void expresso(ArrayList<imAtoken> y)
    {
        String assignedTo = ""; //temp variable
        int posAss = 0; //position of symbol in string
        int relLoc = 0; //location or relative operator
        boolean assFlag = false;
        boolean relflag = false;
        ArrayList<imAtoken> clone = (ArrayList<imAtoken>) y.clone();

        for(int i = 1; i < y.size(); i++)
        {
            if (relopHunt(clone.get(i).name))
            {
                relLoc = i;
                relflag = true;
            }

            if (clone.get(i).name.equals("="))
            {
                posAss = i;
                assFlag = true;
            }
        }
            if (assFlag)
            {
                assignedTo = clone.get(0).name;
                handleFunc(clone);
                handleParens(clone);
                handleMultDiv(clone);
                handleAddSub(clone);

                //Generate another row of code
                operator.add("ASSIGN");
                operand1.add(clone.get(posAss + 1).name);
                operand2.add("");
                result.add(assignedTo);
            }
            else if(relflag)
            {
                rel = clone.get(relLoc).name;

                ArrayList<imAtoken> leftside = new ArrayList<imAtoken>();
                ArrayList<imAtoken> rightside = new ArrayList<imAtoken>();

                //Create left side list
                for(int l = 0; l < relLoc; l++)
                {
                    leftside.add(clone.get(l));
                }

                //Create right side list
                for(int j = relLoc + 1; j < clone.size(); j++)
                {
                    rightside.add(clone.get(j));
                }

                //Left Side Minimizing
                handleParens(leftside);
                handleMultDiv(leftside);
                handleAddSub(leftside);

                //Right Side Minimizing
                handleParens(rightside);
                handleMultDiv(rightside);
                handleAddSub(rightside);

                //Generate another row of code
                operator.add("COMP");
                operand1.add(leftside.get(0).name);
                operand2.add(rightside.get(0).name);
                result.add(makeTemp());
            }
            else
            {
                handleFunc(clone);
                handleParens(clone);
                handleMultDiv(clone);
                handleAddSub(clone);
            }


    }

    public boolean relopHunt(String symbol)
    {
        //Relative operator List
        String[] reList = {"<", "<=", ">", ">=", "==", "!="};

        //search for a relative operator
        for(int i = 0; i < reList.length; i++)
        {
            if(symbol.equals(reList[i]))
            {
                //if found, return true...
                return true;
            }
        }

        //...else return false
        return false;
    }

    public ArrayList<imAtoken> handleFunc(ArrayList<imAtoken> y)
    {
        //start behind the array
        int start = -1;

        //continue searching flag
        boolean search = true;

        //while the search is on...
        while(search)
        {

            for(int i = 0; i < y.size(); i++)
            {
                //if an ID is found followed by a left paren..
                if(y.get(i).type == "ID")
                {
                    if(i+1 < y.size())
                    {
                        if (y.get(i + 1).name.equals("("))
                        {   //the start point is where the ID was found and stop looking for it
                            start = i;
                            break;
                        }
                    }
                }
            }

            //if the start has not moved...
            if(start == -1)
            {
                //stop searching
                search = false;
                continue;
            }

            //for tracking function depth
            int depth = 0;

            //for tracking the location of the end
            int endLoc = 0;

            //something something daaark side
            int count = 0;

            //Track the depth of the expression
            for(int k = start+1; k < y.size(); k++)
            {
                //closing parens decreases the depth
                if(y.get(k).name.equals(")"))
                {
                    depth--;
                }

                //Opening parens increase the depth
                if (y.get(k).name.equals("("))
                {
                    depth++;
                }

                //if the depth is 0 then youve reached the end
                if(depth == 0)
                {
                    endLoc = k;
                    break;
                }
            }

            for(int l = start+1; l < endLoc; l++) //Start+1 to avoid counting function name as an arg
            {
                if (y.get(l).type.equals("ID"))
                {
                    count++;
                }
            }
            if(count!= 0)
            {
                //Create argument quad line
                operator.add("ARG");
                operand1.add("");
                operand2.add("");
                result.add(y.get(endLoc-1).name);
            }

            //Create a function call quad line
            operator.add("CALL");
            operand1.add(y.get(start).name);
            operand2.add(Integer.toString(count));
            result.add(makeTemp());

            //create a new variable inside a token
            imAtoken temp = new imAtoken(gen);

            //add the start point and the token to the arraylist
            y.add(start, temp);

            //clear out the function that has been replaced by the temp variable
            for(int i = endLoc + 1; i > start; i--)
            {
                y.remove(i);
            }

            //reset the start function
            start = -1;
        }

        //return the temp variable
        return y;
    }

    public String relopSym(String op)
    {
        if(op.equals("<"))
        {
            return "BRLT";
        }
        else if(op.equals("<="))
        {
            return "BRLE";
        }
        else if(op.equals(">"))
        {
            return "BRGT";
        }
        else if(op.equals(">="))
        {
            return "BRGE";
        }
        else if(op.equals("=="))
        {
            return "BRE";
        }
        else if(op.equals("!="))
        {
            return "BRNE";
        }
        else
        {
            return "nope";
        }

    }

}
