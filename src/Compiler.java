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

        try
        {
            //Call the Lexical analyzer----------------------------------------
            Lex lex = new Lex(); //Lexical Analyzer creation
            parseMe = lex.run(args); //Create the string from the lexical analyzer

            //Call the Code Generator----------------------------------------
            CodeGen codeGen = new CodeGen(); //Code generator creation
            conti = codeGen.run(parseMe); //run the tokens through a code generator
        }
        catch(Exception e)
        {
            //report file error
            System.out.println("Somethings wrong with the test file, chief\n");

            //print stacktrace
            System.out.println("StackTrace: ");
            e.printStackTrace();

            //exit the program cleanly
            System.exit(0);
        }

        //Print Successful Result-----------------------------------------
        System.out.println("\n" + conti); //If successful, print it to the screen
    }
}
