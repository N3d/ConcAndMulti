package data_structures.implementation;

/*
 * Class necessary to take care of the stamps of each node and state.
 */
public class Stamps{

	public int siStamp;
	public int gsiStamp;
	public int pStamp;
	public int gpStamp;
	public int pLeftStamp;
	public int pRightStamp;
	public int gpLeftStamp;
	public int gpRightStamp;

	public Stamps(int siStamp, int gsiStamp, int pStamp,
			int gpStamp,int pLeftStamp,int pRightStamp,int gpLeftStamp,int gpRightStamp){
		this.siStamp=siStamp;
		this.gsiStamp=gsiStamp;
		this.pStamp=pStamp;
		this.gpStamp=gpStamp;
		this.pLeftStamp=pLeftStamp;
		this.pRightStamp=pRightStamp;
		this.gpLeftStamp=gpLeftStamp;
		this.gpRightStamp=gpRightStamp;
	}
}

