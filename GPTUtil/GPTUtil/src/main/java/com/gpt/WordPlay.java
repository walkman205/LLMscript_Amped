public class WordPlay
{
    private String word;

    public WordPlay(String w)
    { word = w;}

    public void update(String first)
    {word = first + word;}

    public void update(String first, String second)
    {word=word.indexOf(first)+second;}

    public String getWord()
    {return word;}

}
class Test {
    public static void main(String[] args) {
        WordPlay wp = new WordPlay("bandana");
        wp.update("d", "tie");
        wp.update("scarf");
        System.out.println(wp.getWord());
    }
}

