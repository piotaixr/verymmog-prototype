package com.verymmog.nioengine.processor;

import com.verymmog.util.Pair;

import javax.swing.table.AbstractTableModel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LoggingModel extends AbstractTableModel {

    class Element {
        public SocketChannel channel;
        public Float input;
        public Float output;
        public Float total;

        public Object get(int index) {
            switch (index) {
                case 0:
                    return channel;
                case 1:
                    return input;
                case 2:
                    return output;
                case 3:
                    return total;
                default:
                    return null;
            }
        }

        public void set(int index, Object value) {
            switch (index) {
                case 0:
                    channel = (SocketChannel) value;
                case 1:
                    input = (Float) value;
                case 2:
                    output = (Float) value;
                case 3:
                    total = (Float) value;
            }
        }
    }

    private String[] names = {"Channel", "Input", "Output", "Total"};
    private Class[] classes = {SocketChannel.class, Float.class, Float.class, Float.class};
    private List<Element> listData = new ArrayList<>();
    private Map<SocketChannel, Element> mapData = new HashMap<>();
    private Map<SocketChannel, Pair<AtomicLong, AtomicLong>> values = new ConcurrentHashMap<>();
    private long lastTick = new Date().getTime();

    @Override
    public int getRowCount() {
        return mapData.size();
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return names[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return classes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return listData.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        listData.get(rowIndex).set(columnIndex, aValue);
    }

    public void addDataIn(SocketChannel channel, int count) {
        synchronized (values) {
            if (!values.containsKey(channel))
                createValues(channel);
        }

        values.get(channel).first.addAndGet(count);

    }

    public void addDataOut(SocketChannel channel, int count) {
        synchronized (values) {
            if (!values.containsKey(channel))
                createValues(channel);
        }

        values.get(channel).second.addAndGet(count);

    }

    private void createValues(SocketChannel channel) {
        values.put(channel, new Pair<>(new AtomicLong(0), new AtomicLong(0)));
    }

    public void timeTick() {
        long curTick = new Date().getTime();
        long ticksMili = curTick - lastTick;
        lastTick = curTick;

        synchronized (values) {
            for (SocketChannel channel : values.keySet()) {
                Element e = mapData.get(channel);

                if (e == null) {
                    e = new Element();
                    mapData.put(channel, e);
                    listData.add(e);
                    int index = listData.size() - 1;
                    fireTableRowsInserted(index, index);
                }

                e.set(1, new Float(values.get(channel).first.get()));
                e.set(2, new Float(values.get(channel).second.get()));
                e.set(3, new Float(values.get(channel).first.getAndSet(0L) + values.get(channel).second.getAndSet(0)));
                int index = listData.indexOf(e);
                fireTableRowsUpdated(index, index);
            }
        }

    }
}
