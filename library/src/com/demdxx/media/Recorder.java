/**
 * Recorder
 * @author Dmitry Ponomarev <demdxx@gmail.com>
 */

package com.demdxx.media;

import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class Recorder {
  private static String LOG_TAG = Recorder.class.getCanonicalName();

  protected MediaRecorder _recorder = null;
  protected final String _outputFile;
  protected final Delegate _delegate;
  
  protected long _startTime = 0;
  protected long _duration = 0;
  
  /**
   * Constructor
   * @param out
   * @param delegate
   * @throws NullPointerException
   */
  public Recorder(String out, Delegate delegate) throws NullPointerException {
    _outputFile = out;
    _delegate = delegate;

    if (null==_outputFile) {
      throw new NullPointerException("Output File can't be null");
    }
  }
  
  /**
   * Init recorder params
   */
  protected void initRecorder() {
    if (null!=_delegate) {
      _delegate.onRecorderInit(_recorder);
    }
  }
  
  public long getDuration() {
    return _duration + (
        null!=_recorder && _startTime>0
            ? System.currentTimeMillis()-_startTime
            : 0);
  }
  
  /**
   * Start audio recording
   */
  public boolean rec() {
    boolean result = false;
    
    // Init duration time
    _duration = 0;
    _startTime = System.currentTimeMillis();

    // Init recorder
    _recorder = new MediaRecorder();
    initRecorder();

    try {
      _recorder.setOutputFile(_outputFile);
      _recorder.prepare();
      _recorder.start();
      result = true;
    } catch (IOException e) {
      e.printStackTrace();
      Log.e(LOG_TAG, "prepare() failed");
    }
    if (null!=_delegate) {
      _delegate.onRecorderRec(_recorder, result);
    }
    return result;
  }

  /**
   * Stop audio recording
   */
  public void stop() {
    if (null!=_recorder) {
      _recorder.stop();
      _recorder.release();
      _recorder = null;
      
      if (null!=_delegate) {
        _delegate.onRecorderStop();
      }
      
      // Correct duration time
      _duration += System.currentTimeMillis()-_startTime;
      _startTime = 0;
    }
  }
  
  /**
   * Pause recording
   */
  public void pause() {
    if (null!=_recorder) {
      if (null!=_delegate) {
        _delegate.onRecorderPause(_recorder);
      }
      
      // Stop and release object
      _recorder.stop();
      _recorder.release();
      _recorder = null;
      
      // Correct duration time
      _duration += System.currentTimeMillis()-_startTime;
      _startTime = 0;
    }
  }
  
  /**
   * Resume recording
   */
  public boolean resume() {
    boolean result = false;
    _startTime = System.currentTimeMillis();
    _recorder = new MediaRecorder();
    initRecorder();

    try {
        FileOutputStream file = new FileOutputStream(_outputFile);
        _recorder.setOutputFile(file.getFD());
        _recorder.prepare();
        _recorder.start();
        result = true;
    } catch (IOException e) {
        Log.e(LOG_TAG, "prepare() failed");
    }
    if (null!=_delegate) {
      _delegate.onRecorderResume(_recorder, result);
    }
    return result;
  }
  
  public interface Delegate {
    public void onRecorderInit(MediaRecorder mr);
    public void onRecorderRec(MediaRecorder mr, boolean surcess);
    public void onRecorderPause(MediaRecorder mr);
    public void onRecorderResume(MediaRecorder mr, boolean surcess);
    public void onRecorderStop();
  }
}
