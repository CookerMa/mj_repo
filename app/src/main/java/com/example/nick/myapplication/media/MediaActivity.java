package com.example.nick.myapplication.media;

import android.app.Activity;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.example.nick.myapplication.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *  user: majun
 * *  email:maj001@qianhai.com.cn
 * *  time:18-6-13
 **/
public class MediaActivity extends AppCompatActivity implements Player.EventListener {


    private SurfaceView surface;
    DefaultTimeBar bar;
    VideoView videoView;
    private SimpleExoPlayer player;
    PlayerView playerView;
    private ExecutorService executors = Executors.newSingleThreadExecutor();
    SeekBar seekBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.media);
        surface = findViewById(R.id.surface);
        bar = findViewById(R.id.bar);
        videoView = findViewById(R.id.vedio);
        seekBar = findViewById(R.id.seek);
        playerView = findViewById(R.id.play);

        initExo();
    }

    private void initExo() {

        //带宽
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //轨道选择器工厂
        AdaptiveTrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        //创建轨道实例

        DefaultTrackSelector selector = new DefaultTrackSelector(factory);

        player = ExoPlayerFactory.newSimpleInstance(this, selector);


        DefaultDataSourceFactory sourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getApplicationInfo().name));

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

//        Uri uri  = Uri.parse("file:///android_asset/hao.mp4");
        File file = new File(Environment.getExternalStorageDirectory(), "hao.mp4");
        Uri uri = Uri.fromFile(file);
//        MediaSource source = new ExtractorMediaSource(uri, sourceFactory, extractorsFactory, null, null);

        ExtractorMediaSource source = new ExtractorMediaSource.Factory(sourceFactory).createMediaSource(uri);
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(source, 1);
//        player.setVideoSurfaceView(surface);
        player.setVideoSurfaceView(surface);
        player.prepare(loopingMediaSource);


        player.addListener(this);
        continuePlay();//继续播放

//        playerView.setPlayer(player);


//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(file.getPath());
//            mediaPlayer.prepare();
//            Log.e("mj","mp time = " + mediaPlayer.getDuration());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private static final int UP_TIME = 0;//更新播放时间
    private boolean screenFlag = false;//全屏标记
    private boolean playFlag = false;//播放状态
    private final MyHandler myHandler = new MyHandler(this);



    //初始化播放事件
    private void initPlayVideo() {
        //设置总时长
        seekBar.setMax((int) (player.getDuration() / 1000));
        Log.e("kkk","max = "+seekBar.getMax());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //是暂停的开始播放
                if (!player.getPlayWhenReady()) {
                    continuePlay();//继续播放
                }
                player.seekTo(seekBar.getProgress() * 1000);
            }
        });


    }



    //暂停播放
    private void pausePlay() {
        player.setPlayWhenReady(false);
        playFlag = false;
    }

    //继续播放
    private void continuePlay() {
        player.setPlayWhenReady(true);
        //开始读取进度
        playFlag = true;
        executors.execute(runnable);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case PlaybackState.STATE_PLAYING:
                //初始化播放点击事件并设置总时长
                initPlayVideo();
                System.out.println("播放状态: 准备 playing");
                break;

            case PlaybackState.STATE_BUFFERING:
                System.out.println("播放状态: 缓存完成 playing");
                break;

            case PlaybackState.STATE_CONNECTING:
                System.out.println("播放状态: 连接 CONNECTING");
                break;

            case PlaybackState.STATE_ERROR://错误
                System.out.println("播放状态: 错误 STATE_ERROR");
                break;

            case PlaybackState.STATE_FAST_FORWARDING:
                System.out.println("播放状态: 快速传递");
                pausePlay();//暂停播放
                break;

            case PlaybackState.STATE_NONE:
                System.out.println("播放状态: 无 STATE_NONE");
                break;

            case PlaybackState.STATE_PAUSED:
                System.out.println("播放状态: 暂停 PAUSED");
                break;

            case PlaybackState.STATE_REWINDING:
                System.out.println("播放状态: 倒回 REWINDING");
                break;

            case PlaybackState.STATE_SKIPPING_TO_NEXT:
                System.out.println("播放状态: 跳到下一个");
                break;

            case PlaybackState.STATE_SKIPPING_TO_PREVIOUS:
                System.out.println("播放状态: 跳到上一个");
                break;

            case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM:
                System.out.println("播放状态: 跳到指定的Item");
                break;

            case PlaybackState.STATE_STOPPED:
                System.out.println("播放状态: 停止的 STATE_STOPPED");
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    private static class MyHandler extends Handler {
        private final WeakReference<MediaActivity> mActivity;

        private MyHandler(MediaActivity mActivity) {
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity.get() == null) {
                return;
            }
            if (msg.what == UP_TIME) {
                //设置播放进度
                MediaActivity mContext = mActivity.get();
                mContext.seekBar.setProgress(msg.arg1);
            }
        }
    }

    //开启线程读取进度
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            while (playFlag) {
                Message msg = new Message();
                msg.what = UP_TIME;
                //获取播放时间
                msg.arg1 = (int)(player.getCurrentPosition() /1000);
                Log.e("kkk","curr :" +msg.arg1);
                myHandler.sendMessage(msg);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (player != null) {
            playFlag = false;//停止线程
            executors.shutdown();
            player.stop();
            player.release();
            player = null;
        }
        myHandler.removeCallbacksAndMessages(null);
    }

}