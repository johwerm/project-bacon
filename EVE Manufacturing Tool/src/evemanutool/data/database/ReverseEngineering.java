package evemanutool.data.database;

import evemanutool.constants.DBConstants;
import evemanutool.data.general.Time;

public class ReverseEngineering implements DBConstants {
	
	private final double successRate;
	private final Item hybridDec;
	private final int bpcRuns;
	private final Time revTime;
	private final RevPriority prio;
	
	public ReverseEngineering(double successRate, Item hybridDec,
						int bpcRuns, Time revTime, RevPriority prio) {
	
		this.successRate = successRate;
		this.hybridDec = hybridDec;
		this.bpcRuns = bpcRuns;
		this.revTime = revTime;
		this.prio = prio;
	}
	
	public Item getHybridDecryptor() {
		return hybridDec;
	}

	public int getBpcRuns() {
		return bpcRuns;
	}

	public Time getRevTime() {
		return revTime;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public RevPriority getPrio() {
		return prio;
	}
}
