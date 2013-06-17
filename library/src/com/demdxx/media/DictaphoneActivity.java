package com.demdxx.media;

import java.util.Timer;
import java.util.TimerTask;

import com.demdxx.android_dictaphone.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

public abstract class DictaphoneActivity
                extends Activity
                implements View.OnClickListener {

  private static String LOG_TAG = DictaphoneActivity.class.getCanonicalName();
  private static int BUTTONS[] = {R.id.btn_pause, R.id.btn_play, R.id.btn_rec, R.id.btn_stop};

  public static final int TIMER_PERIOD = 1000;
  
  public static final int STATE_NONE = 0;
  public static final int STATE_REC = 1;
  public static final int STATE_REC_PAUSE = 2;
  public static final int STATE_PLAY = 3;
  public static final int STATE_PLAY_PAUSE = 4;
  
  protected AudioRecorder _recorder = null;
  protected AudioPlayer _player = null;
  protected int _state = STATE_NONE;
  
  /////////////////////////////////////////////////////////////////////////////
  // Timer
  //
  protected Timer _timer = null;
  
  protected void startDictaphoneTimer() {
    if (null==_timer) {
      _timer = new Timer();
      _timer.schedule(new TimerTask() {
        @Override
        public void run() {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
              if (isRecording()) {
                onTimeChange(_recorder.getDuration());
              } else if (null != _player) {
                onTimeChange((long)_player.getCurrentPosition());
              }
            }
          });
        }
      }, 0, TIMER_PERIOD);
    }
  }
  
  protected void stopDictaphoneTimer() {
    if (null!=_timer) {
      _timer.cancel();
      _timer.purge();
      _timer = null;
    }
  }

  protected abstract void onTimeChange(long milliseconds);
  
  /////////////////////////////////////////////////////////////////////////////
  // Activity events
  //
  
  @Override
  public void onResume() {
    super.onResume();
    initControls();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    stop();
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // Player values
  //
  
  /**
   * Get output file path
   * @return String
   */
  protected abstract String getOutputFile();
  
  protected AudioRecorder getRecorder() {
    if (null == _recorder) {
      // @INFO: You can not override the methods of the interface in the child, so they were duplicated
      _recorder = new AudioRecorder(getOutputFile(), new AudioRecorder.Delegate() {
        @Override
        public void onRecorderInit(MediaRecorder mr) {
          DictaphoneActivity.this.onRecorderInit(mr);
        }

        @Override
        public void onRecorderRec(MediaRecorder mr, boolean surcess) {
          DictaphoneActivity.this.onRecorderRec(mr, surcess);
        }

        @Override
        public void onRecorderPause(MediaRecorder mr) {
          DictaphoneActivity.this.onRecorderPause(mr);
        }

        @Override
        public void onRecorderResume(MediaRecorder mr, boolean surcess) {
          DictaphoneActivity.this.onRecorderResume(mr, surcess);
        }

        @Override
        public void onRecorderStop() {
          DictaphoneActivity.this.onRecorderStop();
        }
      });
    }
    return _recorder;
  }
  
  protected AudioPlayer getPlayer() {
    if (null == _player) {
      // @INFO: You can not override the methods of the interface in the child, so they were duplicated
      _player = new AudioPlayer(new AudioPlayer.Delegate() {
        @Override
        public void onAudioPlayerInit(MediaPlayer player) {
          DictaphoneActivity.this.onAudioPlayerInit(player);
        }
        
        @Override
        public void onAudioPlayerStart(MediaPlayer player, boolean surcess) {
          DictaphoneActivity.this.onAudioPlayerStart(player, surcess);
        }
        
        @Override
        public void onAudioPlayerResume(MediaPlayer player, boolean surcess) {
          DictaphoneActivity.this.onAudioPlayerResume(player, surcess);
        }
        
        @Override
        public void onAudioPlayerPause(MediaPlayer player) {
          DictaphoneActivity.this.onAudioPlayerPause(player);
        }

        @Override
        public void onAudioPlayerStop(MediaPlayer player) {
          DictaphoneActivity.this.onAudioPlayerStop(player);
        }
      });
    }
    return _player;
  }
  
  public boolean isRecording() {
    return STATE_REC == _state || STATE_REC_PAUSE == _state;
  }
  
  public boolean isPause() {
    return STATE_REC_PAUSE == _state || STATE_PLAY_PAUSE == _state;
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // Record & play actions
  //
  
  public boolean rec() {
    return STATE_NONE==_state && getRecorder().rec();
  }

  public boolean play() {
    return STATE_NONE==_state && getPlayer().play(getOutputFile());
  }
  
  public void pause() {
    if (isRecording()) {
      if (null!=_recorder) {
        _recorder.pause();
      }
    } else if (null!=_player) {
      _player.pause();
    }
  }
  
  public boolean resume() {
    if (isRecording()) {
      if (null!=_recorder) {
        return _recorder.resume();
      }
    } else if (null!=_player) {
      return _player.resume();
    }
    return false;
  }
  
  public void stop() {
    if (isRecording()) {
      if (null!=_recorder) {
        _recorder.stop();
      }
    } else if (null!=_player) {
      _player.stop();
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  
  protected void initControls() {
    Log.d(LOG_TAG, "initControls");
    for (int id : BUTTONS) {
      View v = findViewById(id);
      if (null!=v) {
        v.setOnClickListener(this);
      }
    }
  }

  @Override
  public void onClick(View view) {
    if (R.id.btn_rec == view.getId()) {
      if (STATE_PLAY_PAUSE==_state) { stop(); }
      if (isPause()) { resume(); } else { rec(); }
    } else if (R.id.btn_pause == view.getId()) {
      pause();
    } else if (R.id.btn_play == view.getId()) {
      if (STATE_REC_PAUSE==_state) { stop(); }
      if (isPause()) { resume(); } else { play(); }
    } else if (R.id.btn_stop == view.getId()) {
      stop();
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // Recorder events
  //
  
  protected abstract void onRecorderInit(MediaRecorder mr);

  protected void onRecorderRec(MediaRecorder mr, boolean surcess) {
    if (surcess) {
      startDictaphoneTimer();
      _state = STATE_REC;
    }
  }

  protected void onRecorderPause(MediaRecorder mr) {
    stopDictaphoneTimer();
    _state = STATE_REC_PAUSE;
  }

  protected void onRecorderResume(MediaRecorder mr, boolean surcess) {
    if (surcess) {
      startDictaphoneTimer();
      _state = STATE_REC;
    }
  }

  protected void onRecorderStop() {
    stopDictaphoneTimer();
    _state = STATE_NONE;
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // Player events
  //

  protected abstract void onAudioPlayerInit(MediaPlayer player);

  protected void onAudioPlayerStart(MediaPlayer player, boolean surcess) {
    if (surcess) {
      startDictaphoneTimer();
      _state = STATE_PLAY;
    }
  }

  protected void onAudioPlayerPause(MediaPlayer player) {
    stopDictaphoneTimer();
    _state = STATE_PLAY_PAUSE;
  }

  protected void onAudioPlayerResume(MediaPlayer player, boolean surcess) {
    if (surcess) {
      startDictaphoneTimer();
      _state = STATE_PLAY;
    }
  }

  protected void onAudioPlayerStop(MediaPlayer player) {
    stopDictaphoneTimer();
    _state = STATE_NONE;
  }
}
