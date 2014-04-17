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

    public String toString()
    {
        return name;
    }


}
