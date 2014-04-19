/**
 * Created by Michael Perkins on 2/16/14.
 * customized token for parsing
 */

public class imAtoken
{
    String type;
    String name;
    int depth;
    int line;
    String token = "";

    public imAtoken(String t, String am, int d, int l)
    {
        type  = t;
        name  = am;
        depth = d;
        line  = l;
    }

    public imAtoken(String t)
    {
       token = t; //name
    }

    public String toString()
    {
        return name;
    }


}
