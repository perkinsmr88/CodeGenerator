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

                //add FUNC to the operator List
                operator.add("FUNC");

                //add the functions name to operand1
                operand1.add(token.name);

                //operand2 is the functions return type
                operand2.add(funcType);

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
                operator.add("ALLOC P");

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
                ArrayList<imAtoken> expTokens = new ArrayList<imAtoken>();

                ArrayDeque<imAtoken> y = x.clone();

                while(!y.peek().name.equals(";"))
                {
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

                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {


                                x.pop();

                                statement(x);

                                selectFollow(x);
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

                        expression(x);

                        if(!x.isEmpty())
                        {
                            token = x.peek();

                            if(token.name.equals(")"))
                            {
                                x.pop();

                                statement(x);
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
               // prevAssn = token.name;

                //set operand1 to the current token
               // operand1.add(token.name);

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

                if(op.equals("*"))
                {
                    //Add Multiplication to the operator list
                    operator.add("MUL");
                }
                else if(op.equals("/"))
                {
                    //Add Division to the operator list
                    operator.add("DIV");
                }

                //Operands are blank for the time being
                operand2.add("");
                operand1.add("");

                //Create a temporary variable for storing data
                prevcalcs = makeTemp();

                //make that temp variable the result of the Mul/Div
                result.add(prevcalcs);

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

                //Start EQU line
                operator.add("EQU");

                //Set token to first operand
                operand1.add(token.name);

                //Second operand currently empty
                operand2.add("");

                //result is currently blank
                result.add("");
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
                if(token.name.equals("+"))
                {
                    //Add ADD to operator list
                    operator.add("ADD");
                }
                else if(token.name.equals("-"))
                {
                    //add SUB to operator list
                    operator.add("SUB");
                }

                x.pop();

                term(x);

                //create an iterated temp variable
                prevcalcs = makeTemp();

                //add that variable to the result list
                result.add(prevcalcs);

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

                //Add Assignment to operator list
                operator.add("ASSIGN");

                //Operands are currently empty
                operand1.add(prevAssn);
                operand2.add("");

                //Add the variable to be assigned to the result list
                result.add(tempID);

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
        int startLoc = 0;
        boolean Search = true;

        while (Search) 
        {
            String symbol = "";

            for (int i = 0; i < y.size(); i++) 
            {
                if (y.get(i).name.equals("+") || y.get(i).name.equals("-")) 
                {
                    startLoc = i;
                    symbol = y.get(i).name;
                }
            }

            //if neither +/- is found then there is nothing more to be done here
            if (startLoc == 0) 
            {
                //break the while loop and leave
                Search = false;
                break;
            }

            //Assign operator accordingly
            if (symbol.equals("+")) 
            {
                operator.add("add");
            }
            else if (symbol.equals("-"))
            {
                operator.add("sub");
            }

            //Assign the operands and result
            operand1.add(y.get(startLoc - 1).name);
            operand2.add(y.get(startLoc + 1).name);
            result.add(makeTemp());



            imAtoken replace = new imAtoken("t" + gen);

            //add the temporary token to the list
            y.add(startLoc - 1, replace);
           
            for (int j = 0; j < 3; j++) 
            {
                y.remove(startLoc);
            }

            //reset the startloc
            startLoc = 0;
        }

        return y;
    }

    public ArrayList<imAtoken> handleMultDiv(ArrayList<imAtoken> y)
    {
        int startLoc = 0;
        boolean Search = true;

        while(Search)
        {
            String symbol = "";
            for(int i = 0; i < y.size(); i++)
            {
                if (y.get(i).name.equals("*") || y.get(i).name.equals("/"))
                {
                    startLoc = i;
                    symbol = y.get(i).name;
                }
            }

            if(startLoc == 0)
            {
                Search = false;
                break;
            }

            if(symbol.equals("*"))
            {
                operator.add("mul");
            }
            else if(symbol.equals("/"))
            {
                operator.add("div");
            }

            operand1.add(y.get(startLoc - 1).name);
            operand2.add(y.get(startLoc + 1).name);
            result.add(makeTemp());

            imAtoken replace = new imAtoken("t" + gen);

            y.add(startLoc-1, replace);

            for(int j = 0; j < 3; j++)
            {
                y.remove(startLoc);
            }

            startLoc = 0;
        }

        return y;
    }

    public ArrayList<imAtoken> handleParens(ArrayList<imAtoken> y)
    {
        boolean keepSearching = true;
        int depth = 0;
        int startLoc = 0;
        int endingLoc = 0;


        while(keepSearching)
        {
            for (int i = 0; i < y.size(); i++)
            {
                if (y.get(i).name.equals("("))
                {
                    depth++;
                    startLoc = i;
                }
            }
            if (depth == 0)
                keepSearching = false;
            if (depth != 0)
            {
                for (int k = startLoc; k < y.size(); k++)
                {
                    if (y.get(k).name.equals(")"))
                    {
                        endingLoc = k;
                    }
                }
            }



            ArrayList<imAtoken> working = new ArrayList<imAtoken>();

            for (int i = 1; i < (endingLoc - startLoc); i++)
            {
                working.add(y.get(startLoc + i));
            }

            if (working.size() > 0)
            {
                working = handleMultDiv(working);
                working = handleAddSub(working);

                y.add(startLoc, working.get(0));

                for(int i = endingLoc + 1; i > startLoc; i--)
                {
                    y.remove(i);
                }


            }
            //reset locations and depths
            endingLoc = 0;
            startLoc = 0;
            depth = 0;
        }

        return y;
    }

    public void expresso(ArrayList<imAtoken> y)
    {
        String assignedTo = ""; //temp variable
        int position; //position of symbol in string
        int relLoc; //

        for(int i = 0; i < y.size(); i++)
        {
            if (y.get(i).name.equals("="))
            {
                position = i;
                assignedTo = y.get(0).name;
                handleParens(y);
                handleMultDiv(y);
                handleAddSub(y);

                //Generate another row of code
                operator.add("ASSIGN");
                operand1.add(y.get(position + 1).name);
                operand2.add("");
                result.add(assignedTo);
            }
        }


        for(int i = 0; i < y.size(); i++)
        {
            if(relopHunt(y.get(i).name))
            {
                rel = y.get(i).name;
                relLoc = i;

                ArrayList<imAtoken> leftside = new ArrayList<imAtoken>();
                ArrayList<imAtoken> rightside = new ArrayList<imAtoken>();

                //Create left side list
                for(int l = 0; l < relLoc; l++)
                {
                    leftside.add(y.get(l));
                }

                //Create right side list
                for(int j = relLoc + 1; j < y.size(); j++)
                {
                    rightside.add(y.get(j));
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
        }

    }

    public boolean relopHunt(String symbol)
    {
        //Relative operator List
        String[] reList = {"<", "<=", ">", ">=", "==", "!="};

        //Search for a relative operator
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


}
