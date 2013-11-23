package evemanutool.data.database;

import evemanutool.constants.DBConstants;
import evemanutool.data.general.Time;

public class Invention implements DBConstants{
	
	private final double successRate;
	private final Decryptor dec;
	private final Item metaItem;
	private final int t1BpcRuns;
	private final Time copyTime;
	private final int t2BpcRuns;
	private final Time invTime;
	private final InvPriority prio;
	
	public Invention(double successRate, Decryptor dec, Item metaItem,
						int t1BpcRuns, Time copyTime, int t2BpcRuns, Time invTime, InvPriority prio) {
	
		this.successRate = successRate;
		this.dec = dec;
		this.metaItem = metaItem;
		this.t1BpcRuns = t1BpcRuns;
		this.copyTime = copyTime;
		this.t2BpcRuns = t2BpcRuns;
		this.invTime = invTime;
		this.prio = prio;
	}
	
	public Decryptor getDec() {
		return dec;
	}

	public Item getMetaItem() {
		return metaItem;
	}

	public int getT1BpcRuns() {
		return t1BpcRuns;
	}

	public Time getInvTime() {
		return invTime;
	}

	public int getT2BpcRuns() {
		return t2BpcRuns;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public Time getCopyTime() {
		return copyTime;
	}

	public InvPriority getPrio() {
		return prio;
	}
}
