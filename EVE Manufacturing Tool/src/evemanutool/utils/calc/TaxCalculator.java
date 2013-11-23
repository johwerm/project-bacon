package evemanutool.utils.calc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.beimin.eveapi.shared.wallet.RefType;

import evemanutool.constants.DBConstants;
import evemanutool.data.cache.WalletJournalEntry;

public class TaxCalculator implements DBConstants {

	/*
	 * Calculate the average tax contribution for the given character.
	 */
	public static double calculateAverageWeekTax(long charId, List<WalletJournalEntry> l) {
		double ans = calculateTotalTax(charId, l);
		//Divide by number of weeks between latest and oldest entry.
		ans /= (l.get(0).getDate().getTime() - l.get(l.size() - 1).getDate().getTime()) / ((double) (7 * 24 * 3600 * 1000));
		return ans;
	}

	/*
	 * Calculate the total tax contribution for the given character.
	 */
	public static double calculateTotalTax(long charId, List<WalletJournalEntry> l) {
		double ans = 0;
		Collection<RefType> taxTypes = Arrays.asList(TAX_TYPES);
		for (WalletJournalEntry wJE : l) {
			if (taxTypes.contains(wJE.getType()) && wJE.getSender().getId() == charId) {
				ans += wJE.getAmount();
			}
		}
		return ans;
	}
}
