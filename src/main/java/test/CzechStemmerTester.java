package test;

import stemmer.CzechStemmerAgressive;
import stemmer.CzechStemmerLight;

import java.util.Scanner;

public class CzechStemmerTester {
    public static void main(String[] args) {
        String text = "Co bude následovat po půlnoci, až vyprší ultimátum, které vaše strana ODA dala?\n" +
                "Budeme jednat. Zahájíme jednání s US. Nechceme v žádném případě rozbíjet čtyřkoalici. Záleží na tom, jak Unie svobody zareaguje.\n" +
                "Nebojíte se, že se spor odrazí v poklesu preferencí voličů?\n" +
                "Konflikty mezi partnery mohou poškodit renomé u voličů, ale myslím, že tady žádný fatální konflikt nehrozí. Nechceme se však dohodou za každou cenu dostat do situace, kdy si voliči řeknou, že při řešení problémů jednáme jako ty politické strany, s nimiž soutěžíme.\n" +
                "Co tedy nastane, když US ze svého odmítavého postoje k vašemu návrhu neustoupí?\n" +
                "Tento stav předně nenastal. Jsem přesvědčen, že jednat budeme.\n" +
                "Strany čtyřkoalice podepsaly smlouvu o volební spolupráci, která říká, že do schválených kandidátek lze zasáhnout, jen pokud se na tom dohodnou všechny čtyři subjekty. Není postoj vaší strany porušením dohody?\n" +
                "Je pravda, že tato smlouva existuje, ale své kandidátky každá strana, která bude kandidovat ve volbách, bude teprve podávat. Jsou to dvě věci vedle sebe. Na jedné straně smlouva o kandidátkách, na druhé závazek dluhu. Musí platit obojí. Pokud někdo sám závazky nedodržuje, těžko může chtít po ostatních, aby je dodržovali. Dodržovat smlouvy se musí ve všech případech.";

        Scanner sc = new Scanner(text);
        String current_Word = "";

        while (sc.hasNext()){
            current_Word = sc.next();
            System.out.println(current_Word + " > " + new CzechStemmerLight().stem(current_Word) + " > " + new CzechStemmerAgressive().stem(current_Word));
        }
    }
}
