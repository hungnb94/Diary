package example.com.hb.diary.model;

import java.util.ArrayList;
import java.util.TreeMap;

public class Other {
    TreeMap<Integer, ArrayList<Integer>> map = new TreeMap();
    boolean[] fill;

    int[] prisonForPrincesses(int[] prisons, int[] princesses, int entrance) {
        fill = new boolean[prisons.length];
        int[] res = new int[princesses.length];
        int i = 0;
        for (int capalcity : prisons) {
            ArrayList list = map.get(capalcity);
            if (list == null) list = new ArrayList();
            list.add(i++);
        }
        i = 0;
        for (int prin : princesses) {
            ArrayList<Integer> list = getListForPrince(prin);
            int room;
            if(list==null){
                room=-1;
            } else if (list.size() > 1) {
                room = getSmallerRoom(list, entrance);
            } else {
                room = list.get(0);
            }
            res[i++] = room;
            fill[room] = true;
        }
        return res;
    }

    private int getSmallerRoom(ArrayList<Integer> list, int entrance) {
        int min = Math.abs(list.get(0) - entrance);
        int num = 0;
        for (int room : list) {
            int dis = Math.abs(entrance - room);
            if (dis < min) {
                min = dis;
                num = room;
            }
        }
        return num;
    }

    ArrayList<Integer> getListForPrince(int prin) {
        ArrayList<Integer> list = findEqual(prin);
        if (list!=null) return list;
        return findBigger(prin);
    }

    ArrayList<Integer> findBigger(int prin) {
        for (int cap : map.keySet()) {
            if (cap > prin) {
                ArrayList<Integer> list = map.get(cap);
                ArrayList arr = new ArrayList();
                for (int num : list) {
                    if (!fill[num]) arr.add(num);
                }
                if (arr.size() > 0) return arr;
            }
        }
        return null;
    }

    ArrayList<Integer> findEqual(int prin) {
        ArrayList<Integer> list = map.get(prin);
        ArrayList res = new ArrayList();
        if(list==null) return null;
        for (int num : list) {
            if (!fill[num]) res.add(num);
        }
        return res;
    }
}
