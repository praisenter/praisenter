import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;


public class DumpLanguages {
	public static void main(String[] args) {
		Locale[] locales = Locale.getAvailableLocales();
		Arrays.sort(locales, new Comparator<Locale>() {
			@Override
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		for (Locale locale : locales) {
			System.out.println(locale.getDisplayName() + " " + locale.getLanguage() + "_" + locale.getCountry() + "_" + locale.getVariant());
		}
	}
}
