/*
 * Copyright (C) 2012 Christopher Eby <kreed@kreed.org>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.kreed.vanilla;

import android.content.*;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.util.Map;

/**
 * A preference that allows the MediaScanner to be triggered.
 */
public class ExportRatingsPreference extends Preference {
    //Context context;

	/*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
				setSummary(R.string.scan_in_progress);
				setEnabled(false);
			} else if (intent.getAction().equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
				setSummary(R.string.finished_scanning);
				setEnabled(true);
				context.unregisterReceiver(this);
			}
		}
	};
         */

	public ExportRatingsPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setTitle(R.string.export_xml);
		setSummary(R.string.tap_to_export);

	}

	@Override
	public void onClick()
	{
		/*IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addDataScheme("file");
		getContext().registerReceiver(mReceiver, intentFilter);

		Uri storage = Uri.parse("file://" + Environment.getExternalStorageDirectory());
		getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, storage));
		*/
        SharedPreferences settings = getContext().getSharedPreferences(FullPlaybackActivity.RATINGS_NAME, 0);
        Map<String, ?> allRatings=settings.getAll();

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        String XmlData;
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "ratings");
            serializer.attribute("", "number", String.valueOf(allRatings.keySet().size()));
            serializer.attribute("", "version", "0.1");
            for (String key: allRatings.keySet()){
                serializer.startTag("", "rating");
                serializer.attribute("", "path", key);
                serializer.attribute("", "rating", allRatings.get(key).toString());
                serializer.endTag("", "rating");
            }
            serializer.endTag("", "ratings");
            serializer.endDocument();
            XmlData= writer.toString();

            // Create a path where we will place our private file on external
            // storage.
            File file = new File(getContext().getExternalFilesDir(null), "Ratings.xml");

            try {
                // Very simple code to copy a picture from the application's
                // resource into the external file.  Note that this code does
                // no error checking, and assumes the picture is small (does not
                // try to copy it in chunks).  Note that if external storage is
                // not currently mounted this will silently fail.
                OutputStream os = new FileOutputStream(file);
                os.write(XmlData.getBytes());
                os.close();
            } catch (IOException e) {
                // Unable to create file, likely because external storage is
                // not currently mounted.
                Log.w("ExternalStorage", "Error writing " + file, e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //return settings.getLong("o", -1);
    }
}
