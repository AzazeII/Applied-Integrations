package AppliedIntegrations.grid;

import AppliedIntegrations.API.Storage.IAEEnergyStack;
import appeng.api.storage.data.IAEStack;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @Author Azazell
 */
public class EnergyIterator<T extends IAEStack> implements Iterator<T> {
    private Iterator<T> parent;
    private T next;

    public EnergyIterator(Iterator<T> iterator) {
        this.parent = iterator;
    }

    @Override
    public boolean hasNext() {
        while (this.parent.hasNext()) {
            this.next = this.parent.next();
            if (this.next.isMeaningful()) {
                return true;
            } else {
                this.parent.remove();
            }
        }

        this.next = null;
        return false;
    }

    @Override
    public T next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }

        return this.next;
    }

    @Override
    public void remove() {
        this.parent.remove();
    }
}
