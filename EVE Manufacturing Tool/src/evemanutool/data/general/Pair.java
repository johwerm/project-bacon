package evemanutool.data.general;

public class Pair<L, R> {

	private final L fst;
	private final R snd;

	public Pair(L fst, R snd) {
		this.fst = fst;
		this.snd = snd;
	}

	public L getFst() {
		return fst;
	}

	public R getSnd() {
		return snd;
	}

	@Override
	public int hashCode() {
		return fst.hashCode() ^ snd.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Pair))
			return false;
		Pair<?, ?> pairo = (Pair<?, ?>) o;
		return this.fst.equals(pairo.getFst())
				&& this.snd.equals(pairo.getSnd());
	}

}