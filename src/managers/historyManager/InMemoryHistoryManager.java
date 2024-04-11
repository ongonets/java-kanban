package managers.historyManager;

import task.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    LinkedList<Task> history;
    Map<Integer, Node<Task>> historyByID;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
        historyByID = new HashMap<>();
    }

    public class LinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public Node<T> linkLast(T task) {
            if (size != 0) {
                Node<T> oldTail = tail;
                Node<T> newNode = new Node<>(oldTail, task, null);
                tail = newNode;
                if (oldTail != null) {
                    oldTail.next = newNode;
                }
                size++;
                return newNode;
            } else {
                Node<T> newNode = new Node<>(null, task, tail);
                head = newNode;
                tail = newNode;
                size++;
                return newNode;
            }


        }


        public int size() {
            return this.size;
        }

        public List<T> getTasks() {
            List<T> historyList = new ArrayList<>();
            Node<T> currentNode = head;
            while (currentNode != null) {
                historyList.add(currentNode.task);
                currentNode = currentNode.next;
            }
            return historyList;
        }

        public void removeNode(Node<T> node) {
            Node<T> oldNext = node.next;
            Node<T> oldPrev = node.prev;
            if (oldNext != null) {
                oldNext.prev = oldPrev;
            } else {
                tail = oldPrev;
            }
            if (oldPrev != null) {
                oldPrev.next = oldNext;
            } else {
                head = oldNext;
            }
            size--;
        }


    }


    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        if (historyByID.containsKey(task.getTaskID())) {
            remove(task.getTaskID());
        }
        Node<Task> newNode = history.linkLast(task);
        historyByID.put(task.getTaskID(), newNode);
    }

    @Override
    public void remove(int taskID) {
        Node<Task> removeNode = historyByID.get(taskID);
        history.removeNode(removeNode);
        historyByID.remove(taskID);
    }
}