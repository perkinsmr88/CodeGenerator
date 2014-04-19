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

    public imAtoken(String t, String am, int d, int l)
    {
        type  = t;
        name  = am;
        depth = d;
        line  = l;
    }

    public imAtoken(String t)
    {
       name = t; //name
        type = "ID";
        depth = -1;
        line = -1;
    }

    public String toString()
    {
        return name;
    }


}
