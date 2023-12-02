package lych.necromancer.capability;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.capabilities.CapabilityProvider;

import java.util.NoSuchElementException;

@AutoRegisterCapability
public interface IDarkPowerStorage {
    int getDarkPower();

    void setDarkPower(int amount);

    int getMaxStorage();

    @CheckReturnValue
    default int getMaxChange(int amount) {
        return changeDarkPower(amount, true);
    }

    @CanIgnoreReturnValue
    default int changeDarkPower(int amount) {
        return changeDarkPower(amount, false);
    }

    private int changeDarkPower(int amount, boolean simulate) {
        int dp = getDarkPower();
        if (simulate) {
            if (dp < -amount) { // Amount is negative and its absolute value is bigger than dp
                return -dp;
            } else if (amount + dp > getMaxStorage()) {
                return getMaxStorage() - dp;
            } else {
                return amount;
            }
        }
        setDarkPower(dp + amount);
        return getDarkPower() - dp;
    }

    default int transferTo(IDarkPowerStorage another, int amount) {
        int maxChange = getMaxChange(amount);
        int anotherMaxChange = another.getMaxChange(maxChange);
        changeDarkPower(-anotherMaxChange);
        another.changeDarkPower(anotherMaxChange);
        return anotherMaxChange;
    }

    default boolean extractDarkPower(int amount) {
        if (amount <= 0) {
            return true;
        }
        if (getDarkPower() < amount) {
            return false;
        }
        changeDarkPower(-amount);
        return true;
    }

    static IDarkPowerStorage of(@SuppressWarnings("UnstableApiUsage") CapabilityProvider<?> provider) {
        return provider.getCapability(ModCapabilities.DARK_POWER_STORAGE).orElseThrow(NoSuchElementException::new);
    }
}
