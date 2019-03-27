package pow.jie.zdownloader.util;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import pow.jie.zdownloader.bean.ThreadInfo;

public class DatabaseUtil {

    public void saveThread(ThreadInfo threadInfo) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm0 -> realm.copyToRealm(threadInfo));
    }

    public void deleteThread(int id) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ThreadInfo> threadInfo = realm.where(ThreadInfo.class).equalTo("id", id).findAll();
        realm.executeTransaction(realm1 -> threadInfo.deleteAllFromRealm());
    }

    public void updateThread(int id, long finished) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ThreadInfo> threadInfo = realm.where(ThreadInfo.class).equalTo("id", id).findAll();
        realm.executeTransaction(realm2 -> threadInfo.setLong("finished", finished));
    }

    public List<ThreadInfo> queryThread(int id) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ThreadInfo> threadInfo = realm.where(ThreadInfo.class).equalTo("id", id).findAll();
        return new ArrayList<>(threadInfo);
    }

    public boolean isThreadExists(int id) {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ThreadInfo> threadInfo = realm.where(ThreadInfo.class).equalTo("id", id).findAll();
        return !(threadInfo.size() == 0);
    }
}
