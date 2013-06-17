/**
 * AudioPlayer
 * @author Dmitry Ponomarev <demdxx@gmail.com>
 */

package com.demdxx.media;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer implements MediaPlayer.OnCompletionListener {
  private static String LOG_TAG = AudioPlayer.class.getCanonicalName();

  protected MediaPlayer _player = null;
  protected Delegate _delegate = null;
  protected String _playFile = null;
  
  public AudioPlayer(Delegate delegate) {
    _delegate = delegate;
  }
  
  public AudioPlayer(String file, Delegate delegate) {
    _delegate = delegate;
    _playFile = file;
  }
  
  public int getCurrentPosition() {
    return null==_player ? 0 : _player.getCurrentPosition();
  }
  
  public int getDuration() {
    return null==_player ? 0 : _player.getDuration();
  }
  
  /**
   * Init player params
   */
  protected void initPlayer() {
    if (null == _player) {
      throw new NullPointerException("Player link can't be null");
    }
    if (null != _delegate) {
      _delegate.onAudioPlayerInit(_player);
    }
    _player.setOnCompletionListener(this);
    _player.setAudioStreamType(AudioManager.STREAM_MUSIC);
  }
  
  /**
   * Play audio file
   * @param file
   * @param position
   * @return boolean
   */
  public boolean play(String file, int position) {
    _playFile = file;
    return play(position);
  }
  
  /**
   * Play audio file
   * @param file
   * @return boolean
   */
  public boolean play(String file) {
    _playFile = file;
    return play(-1);
  }
  
  /**
   * Play audio file
   * @return boolean
   */
  public boolean play() {
    return play(-1);
  }
  
  /**
   * Play audio file
   * @param position
   * @return boolean
   */
  public boolean play(int position) {
    if (null==_playFile) {
      throw new NullPointerException("Play file can't be null");
    }
    stop();
    
    boolean surcess = false;
    _player = new MediaPlayer();
    initPlayer();
    
    try {
      _player.setDataSource(_playFile);
      _player.prepare();
      if (position>=0) {
        _player.seekTo(position);
      }
      _player.start();
      surcess = true;
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (null != _delegate) {
      _delegate.onAudioPlayerStart(_player, surcess);
    }
    
    Log.d(LOG_TAG, "Play: "+_playFile);
    
    return surcess;
  }
  
  /**
   * Stop playing
   */
  public void stop() {
    if (null != _player) {
      if (null != _delegate) {
        _delegate.onAudioPlayerStop(_player);
      }
      _player.stop();
      _player.release();
      _player = null;
    }
  }
  
  /**
   * Pause
   */
  public void pause() {
    if (null != _player) {
      int p = _player.getCurrentPosition();
      _player.pause();
      _player.seekTo(p);
      if (null != _delegate) {
        _delegate.onAudioPlayerPause(_player);
      }
    }
  }
  
  /**
   * Resume
   * @return boolean
   */
  public boolean resume() {
    boolean result = false;
    if (null != _player) {
      _player.start();
      result = true;
    }
    if (null != _delegate) {
      _delegate.onAudioPlayerResume(_player, result);
    }
    return result;
  }
  
  /**
   * Set playing position
   * @param msec
   */
  public void seekTo(int msec) {
    if (null != _player) {
      _player.seekTo(msec);
    }
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    if (null != _delegate) {
      _delegate.onAudioPlayerStop(mp);
    }
  }
  
  public interface Delegate {
    public void onAudioPlayerInit(MediaPlayer player);
    public void onAudioPlayerStart(MediaPlayer player, boolean surcess);
    public void onAudioPlayerPause(MediaPlayer player);
    public void onAudioPlayerResume(MediaPlayer player, boolean surcess);
    public void onAudioPlayerStop(MediaPlayer player);
  }
}
