package evemanutool.utils.calc;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;

import evemanutool.constants.DBConstants.IndustryActivity;
import evemanutool.data.cache.BlueprintAssetEntry;
import evemanutool.data.database.Blueprint;

public class IndustryJobCalculator {
	
	public static void updateWithIndustryJob(ApiIndustryJob iJ, BlueprintAssetEntry ba, Blueprint b) {

		//Temporary variables.
		int me = 0;
		int pe = 0;
		//(Now - JobStart) / (JobFinish - JobStart).
		double completionFactor = (double) (System.currentTimeMillis() - iJ.getBeginProductionTime().getTime()) /
				(iJ.getEndProductionTime().getTime() - iJ.getBeginProductionTime().getTime());
		
		//Set to 0 if negative (Job is pending).
		completionFactor = completionFactor < 0 ? 0 : completionFactor;
		//Set to 1 if > 1 (Job is already finished).
		completionFactor = completionFactor > 1 ? 1 : completionFactor;
		
		//Set ME/PE levels.
		me = iJ.getInstalledItemMaterialLevel();
		pe = iJ.getInstalledItemProductivityLevel();
		
		if (iJ.getActivityID() == IndustryActivity.TIME.key) {
			pe = getResearchLevel(iJ, pe, b.getPeTime(), completionFactor);
		}else if (iJ.getActivityID() == IndustryActivity.MATERIAL.key) {
			me = getResearchLevel(iJ, me, b.getMeTime(), completionFactor);
		}
		
		//Update levels.
		updateBPOStats(ba, me, pe);
	}
	
	public static int getResearchLevel(ApiIndustryJob ij, int startLevel, int researchTime, double completionFactor) {
		return (int) (startLevel + 
				(completionFactor * 
				(ij.getEndProductionTime().getTime() - ij.getBeginProductionTime().getTime()) / 
				(researchTime * ij.getTimeMultiplier() * ij.getCharTimeMultiplier() * 1000))
				+ 0.5);
	}
	
	public static void updateBPOStats(BlueprintAssetEntry ba, int me, int pe) {
		if (me > ba.getMe()) {
			ba.setMe(me);
		}
		if (pe > ba.getPe()) {
			ba.setPe(pe);
		}
	}
}
