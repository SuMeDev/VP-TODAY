package vortex.vp_today.net;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import es.dmoral.toasty.Toasty;
import vortex.vp_today.activity.MainActivity;
import vortex.vp_today.logic.VPInfo;
import vortex.vp_today.logic.VPRow;
import vortex.vp_today.util.TriTuple;
import vortex.vp_today.util.Util;

/**
 * @author Simon Dräger
 * @version 7.3.18
 */

// params: Object, progress: Void, return: TriTuple: String Integer String[] || String Integer VPInfo[]
public class RetrieveVPTask extends AsyncTask<Object, Void, TriTuple<String, Integer, VPInfo>> {
    private Exception exception = null;
    private MainActivity main = null;

    // params: MainActivity, String date (vp style), String stufe, String sub, String[] kurse
    @Override
    protected TriTuple<String, Integer, VPInfo> doInBackground(Object... params) {
        main = (MainActivity) params[0];
        String vpDate = (String) params[1];
        String stufe = (String) params[2];
        String sub = (String) params[3];
        String[] kurse = (String[]) params[4];

        try {
            Log.i("RetrieveVPTask", "refreshing = true");

            String unfiltered = Util.fetchUnfiltered(vpDate);

            Log.i("RetrieveVPTask", "got unfiltered: " + (unfiltered == null ? "null" : "not null"));

            Document doc = Jsoup.parse(unfiltered);

            Log.i("RetrieveVPTask", "got doc");

            TriTuple<String, Integer, String[]> filtered = null;
            TriTuple<String, Integer, VPInfo> filteredInfo = null;

            if (kurse == null) {
                Log.i("RVPT/doInBackground", "kurse = null, doing filterHTML sub");
                filtered = Util.filterHTML(doc, stufe, sub);
            } else {
                Log.i("RVPT/doInBackground", "kurse not null, doing filterHTML kurse");
                filteredInfo = Util.filterHTML(doc, stufe, kurse);
            }

            if (filtered == null) {
                if (filteredInfo == null) {
                    Log.i("RDT/doInBackground", "filtered & filteredInfo = null");
                    return null;
                } else {
                    Log.i("RDT/doInBackground", "Chose filteredInfo, not null");
                }
            } else {
                Log.i("RDT/doInBackground", "filtered != null msgotd = " + filtered.x);
            }

            Log.i("RDT/doInBackground", "filteredInfo" + (filteredInfo == null));

            try {
                if (filtered != null) {
                    if (filtered.z == null) {
                        Log.i("doInBackground", "filtered.z = null, return novpexist");
                        return new TriTuple<String, Integer, VPInfo>("novpexist", 0, null);
                    }
                } else if (filteredInfo != null) {
                    if (filteredInfo.z == null) {
                        Log.i("doInBackground", "filteredInfo.z = null, return novpexist");
                        return new TriTuple<String, Integer, VPInfo>("novpexist", 0, null);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i("doInBackground", "z = null, return novpexist");
                return new TriTuple<String, Integer, VPInfo>("novpexist", 0, null);
            }

            if (filtered != null) {
                TriTuple<String, Integer, VPInfo> out = null;
                VPInfo info = new VPInfo();
                VPRow[] rows = new VPRow[filtered.z.length];

                for (int i = 0; i < rows.length; i++) {
                    VPRow r = new VPRow();
                    r.setContent(filtered.z[i]);
                    rows[i] = r;
                }

                info.addAll(rows);

                out = new TriTuple<>(filtered.x, filtered.y, info);

                return out;
            }
            return filteredInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
            this.exception = ex;
        }
        return null;
    }

    @Override
    protected void onPostExecute(TriTuple<String, Integer, VPInfo> result) {
        try {
            Log.i("onPostExecute", "setting text to result");
            if (result != null) {
                Log.i("onPostExecute", "result != null");
                try {
                    Log.i("onPostExecute", "result: " + result.z.getRows().get(0).toString());
                } catch (Exception ex) {
                    Log.i("onPostExecute", "result: null");
                }
                //if (result.z != null)
                //Log.i("doInBackground", "filtered.z.length = " + result.z.length);
                if (result.x.equals("novpexist")) {
                    main.tvVers.setText("Version: 0");
                    main.msgOTD.setText("Für diesen Tag gibt es noch keine Vertretungen!");
                } else if (result.x != null && result.z != null && !result.z.isEmpty()) {
                    Log.i("onPostExecute", "result not empty");
                    main.txt.setText("");
                    if (result.z.assumeKursVersion()) {
                        Log.i("onPostExecute", "assuming kurse version");
                        for (VPRow row : result.z.getRows()) {
                            Log.i("onPostExecute", "adding row: " + row.getLinearContent());
                            main.txt.append(row.getLinearContent());
                        }
                    } else {
                        Log.i("onPostExecute", "not assuming, adding result.z.getContent");
                        main.txt.setText(TextUtils.join("\n\n", result.z.getContent()));
                    }
                    main.msgOTD.setText(result.x);
                    main.tvVers.setText("Version: " + result.y.intValue());
                } else {
                    Log.i("onPostExecute", "in else: result.x = null -> " + (result.x == null) + " y = 0 -> " + (result.y == 0) + " z = null -> " + (result.z == null));
                }
            } else {
                Log.i("onPostExecute", "result = null");
                Toasty.error(main.getApplicationContext(), "Fehler beim Aktualisieren!").show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Log.i("onPostExecute", "set text to result");
        main.swipe.setRefreshing(false);
        Log.i("onPostExecute", "set refreshing to false");
    }

    @Nullable
    public Exception getException() {
        return exception;
    }
}
