package com.vgb;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Arrays;

/**
 * @author sheltonbumhe
 * A generic sorted list that keeps elements ordered using a provided Comparator.
 * Supports adding, removing, and iterating over elements.
 */
public class SortedList<E> implements Iterable<E> {
    private E[] elements;                 
    private int size;                     
    private Comparator<E> comparator;     
    private static final int INITIAL_CAPACITY = 10; 

    @SuppressWarnings("unchecked")
    public SortedList(Comparator<E> comparator) {
        this.elements = (E[]) new Object[INITIAL_CAPACITY];
        this.size = 0;
        this.comparator = comparator;
    }

    /**
     * Returns the number of elements in the list.
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the list is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the element at the given index.
     * Throws IndexOutOfBoundsException if index is invalid.
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return elements[index];
    }

    /**
     * Adds a new element to the list, keeping the list sorted.
     */
    public void add(E element) {
        ensureCapacity();                      // Make sure there's space
        int insertIndex = findInsertPosition(element); // Find correct spot
        shiftRight(insertIndex);               // Shift elements to make space
        elements[insertIndex] = element;       // Insert new element
        size++;
    }

    /**
     * Removes the given element from the list if it exists.
     * Returns true if removed, false if not found.
     */
    public boolean remove(E element) {
        int index = indexOf(element);
        if (index == -1) return false;         // Not found
        shiftLeft(index);                      // Shift elements to close gap
        size--;
        return true;
    }

    /**
     * Doubles array capacity when needed.
     */
    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }

    /**
     * Finds the index where a new element should be inserted.
     */
    private int findInsertPosition(E element) {
        int i = 0;
        while (i < size && comparator.compare(elements[i], element) < 0) {
            i++;
        }
        return i;
    }

    /**
     * Shifts elements right from the given index to make space.
     */
    private void shiftRight(int index) {
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
    }

    /**
     * Shifts elements left from the given index to remove a gap.
     */
    private void shiftLeft(int index) {
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[size - 1] = null;
    }

    /**
     * Finds the index of the given element.
     * Returns -1 if not found.
     */
    private int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns an iterator over the elements in the list.
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < size;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return elements[current++];
            }
        };
    }
}
