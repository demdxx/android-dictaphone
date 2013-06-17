/**
 * AudioRecorder
 * @author Dmitry Ponomarev <demdxx@gmail.com>
 */

package com.demdxx.media;

import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder extends Recorder {
  private static String LOG_TAG = AudioRecorder.class.getCanonicalName();
  
  /**
   * Constructor
   * @param out
   * @param delegate
   * @throws NullPointerException
   */
  public AudioRecorder(String out, Delegate delegate) throws NullPointerException {
    super(out, delegate);
  }
  
  /**
   * Init recorder params
   */
  protected void initRecorder() {
    Log.d(LOG_TAG, "initRecorder");
    _recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    _recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
    super.initRecorder();
  }
}
