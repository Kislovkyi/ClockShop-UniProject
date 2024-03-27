package clockshop.repair;

public enum RepairType {
    QUICK(150),
    NORMAL(100),
    MAINTENANCE(0),
    RADIO(300);

    private int cost;

    RepairType(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
	void setCost(int cost) {
        this.cost = cost;
    }
}