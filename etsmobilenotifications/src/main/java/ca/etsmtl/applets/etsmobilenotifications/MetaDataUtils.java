package ca.etsmtl.applets.etsmobilenotifications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Class used to manage meta-data values from AndroidManifest.xml
 *
 * @see <a href="https://developer.android.com/guide/topics/manifest/meta-data-element">https://developer.android.com/guide/topics/manifest/meta-data-element</a>
 *
 * Created by Sonphil on 20-04-19.
 */

final class MetaDataUtils {
    private static final String META_DATA_PREFIX = "ca.etsmtl.applets.etsmobilenotifications";

    static String getValue(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            return bundle.getString(META_DATA_PREFIX + name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
