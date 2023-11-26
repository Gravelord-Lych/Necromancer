package lych.necromancer.capability;

public enum EmptyDarkPowerStorage implements IDarkPowerStorage {
    INSTANCE;

    @Override
    public int getDarkPower() {
        return 0;
    }

    @Override
    public void setDarkPower(int amount) {}

    @Override
    public int getMaxStorage() {
        return 0;
    }
}
