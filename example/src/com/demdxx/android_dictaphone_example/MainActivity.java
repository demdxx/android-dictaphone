package com.demdxx.android_dictaphone_example;

import com.demdxx.media.DictaphoneActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends DictaphoneActivity implements SeekBar.OnSeekBarChangeListener {
  
  private static final String LOG_TAG = MainActivity.class.getCanonicalName();

  protected Button _btnRec;
  protected Button _btnPause;
  protected Button _btnStop;
  protected Button _btnPlay;
  protected SeekBar _seekBar;
  protected TextView _time;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    _btnRec = (Button)findViewById(R.id.btn_rec);
    _btnPause = (Button)findViewById(R.id.btn_pause);
    _btnStop = (Button)findViewById(R.id.btn_stop);
    _btnPlay = (Button)findViewById(R.id.btn_play);
    _seekBar = (SeekBar)findViewById(R.id.seek);
    _time = (TextView)findViewById(R.id.time);
    
    _seekBar.setVisibility(View.GONE);
    _seekBar.setOnSeekBarChangeListener(this);
  }
  
  /////////////////////////////////////////////////////////////////////////////
  // Recorder events
  //

  @Override
  public void onRecorderInit(MediaRecorder mr) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onRecorderRec(MediaRecorder mr, boolean surcess) {
    super.onRecorderRec(mr, surcess);
    if (surcess) {
      _btnRec.setEnabled(false);
      _btnStop.setEnabled(true);
      _btnPause.setEnabled(true);
      _btnPlay.setEnabled(false);
      _btnPlay.setVisibility(View.GONE);
      _seekBar.setVisibility(View.GONE);
    }
  }

  @Override
  public void onRecorderPause(MediaRecorder mr) {
    super.onRecorderPause(mr);
    _btnRec.setEnabled(true);
    _btnStop.setEnabled(false);
    _btnPause.setEnabled(false);
    _btnPlay.setEnabled(false);
  }

  @Override
  public void onRecorderResume(MediaRecorder mr, boolean surcess) {
    super.onRecorderResume(mr, surcess);
    onRecorderRec(mr, surcess);
  }

  @Override
  public void onRecorderStop() {
    super.onRecorderStop();
    _btnRec.setEnabled(true);
    _btnRec.setVisibility(View.VISIBLE);
    _btnStop.setEnabled(false);
    _btnPause.setEnabled(false);
    _btnPlay.setEnabled(true);
    _btnPlay.setVisibility(View.VISIBLE);
  }

  /////////////////////////////////////////////////////////////////////////////
  // Audio Player events
  //

  @Override
  public void onAudioPlayerInit(MediaPlayer player) {
    player.setVolume(1, 1);
  }

  @Override
  public void onAudioPlayerStart(MediaPlayer player, boolean surcess) {
    super.onAudioPlayerStart(player, surcess);
    if (surcess) {
      _btnStop.setEnabled(true);
      _btnPause.setEnabled(true);
      _btnPlay.setEnabled(false);
      _seekBar.setVisibility(View.VISIBLE);
      _seekBar.setEnabled(true);
      _seekBar.setMax(player.getDuration()/10);
    }
  }

  @Override
  public void onAudioPlayerPause(MediaPlayer player) {
    super.onAudioPlayerPause(player);
    _btnStop.setEnabled(true);
    _btnPause.setEnabled(false);
    _btnPlay.setEnabled(true);
  }

  @Override
  public void onAudioPlayerResume(MediaPlayer player, boolean surcess) {
    super.onAudioPlayerResume(player, surcess);
    if (surcess) {
      _btnStop.setEnabled(true);
      _btnPause.setEnabled(true);
      _btnPlay.setEnabled(false);
      _seekBar.setVisibility(View.VISIBLE);
      _seekBar.setEnabled(true);
      _seekBar.setMax(player.getDuration()/10);
    }
  }

  @Override
  public void onAudioPlayerStop(MediaPlayer player) {
    super.onAudioPlayerStop(player);
    _btnRec.setVisibility(View.VISIBLE);
    _btnRec.setEnabled(true);
    _btnStop.setEnabled(false);
    _btnPause.setEnabled(false);
    _btnPlay.setEnabled(true);
    _seekBar.setProgress(player.getCurrentPosition()/10);
  }

  /////////////////////////////////////////////////////////////////////////////

  @Override
  protected String getOutputFile() {
    return Environment.getExternalStorageDirectory()+"/test.3gpp";
  }

  @Override
  protected void onTimeChange(long milliseconds) {
    Log.d(LOG_TAG, "onTimeChange: "+milliseconds);
    int seconds = (int)milliseconds/1000;
    _time.setText(String.format("%02d:%02d:%02d", seconds/3600, (seconds%3600)/60, seconds%60));
    if (_doChange) {
      _seekBar.setProgress((int)milliseconds/10);
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // SeekBar change events
  //
  
  boolean _doChange = true;
  int _newPosition = 0;
  
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    _newPosition = progress * 10;
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    _doChange = false;
    _newPosition = 0;
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    _player.seekTo(_newPosition);
    _doChange = true;
  }

}
