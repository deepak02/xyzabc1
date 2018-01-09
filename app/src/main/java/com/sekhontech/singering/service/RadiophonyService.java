package com.sekhontech.singering.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.sekhontech.singering.Activities.Player;
import com.sekhontech.singering.Models.Explore_model_item;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class RadiophonyService extends Service {

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int NOTIFICATION_ID = 1;
    public static ExoPlayer exoPlayer;
    public static int list;
    static ProgressDialog dialog;
    static ProgressTask task;
    private static MediaCodecAudioTrackRenderer audioRenderer;
    private static Context context;
    private static int inwhich;
    private static RadiophonyService service;
    private static Explore_model_item station;
    private WifiLock wifiLock;
    private String userAgent;
    private Uri uri;
    public ExoPlayer.Listener onCompletionListener = new ExoPlayer.Listener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == ExoPlayer.STATE_ENDED) {
                Log.e("---state----", "get" + RadiophonyService.exoPlayer.getPlaybackState());
                Log.e("---current pos----", "get" + RadiophonyService.exoPlayer.getCurrentPosition());
                Player inst = Player.instance();
                if (Player.clicked == true) {
                } else {
                    try {
                        inst.Play_next_song();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e("OUT OF BOUNDS", "" + e);
                    }
                    RadiophonyService.exoPlayer.removeListener(this);
                }
            }
        }

        @Override
        public void onPlayWhenReadyCommitted() {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.e("ExoplayerExcption", "" + error);
            Toast.makeText(context, "No Track Found", Toast.LENGTH_SHORT).show();
            Player inst = Player.instance();

            if (Player.clicked == true) {
            } else {
                try {
                    inst.Play_next_song();


                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e("OUT OF BOUNDS", "" + e);
                }
                RadiophonyService.exoPlayer.removeListener(this);
            }
        }
    };

    /*    public static double startTime = 0;
        public static double finalTime = 0;*/
    private Visualizer mVisualizer;

    static public void initialize(Context context, Explore_model_item station, int inwhich) {
        RadiophonyService.context = context;
        RadiophonyService.station = station;
        RadiophonyService.inwhich = inwhich;
        //exoPlayer = ExoPlayer.Factory.newInstance(1);
        Log.e("inwhich", "" + inwhich);
    }

    static public RadiophonyService getInstance() {
        if (service == null) {
            service = new RadiophonyService();
        }
        return service;
    }

    public static void Mute() {
        exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, 0f);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        RadiophonyService.exoPlayer.addListener(onCompletionListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (station.getName() != null) {
                task = new ProgressTask();
                task.execute(station.getName());
            } else {
                Log.e("No track found", "NO TRACK FOUND");
            }
        } catch (RuntimeException e) {
            Log.e("Error", String.valueOf(e));
        }

        return START_NOT_STICKY;
    }

    public void pause() {
        // exoPlayer.stop();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public void start() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            Log.e("Destroyed", "Called");
        }
    }

    public void onStop() {
        /*if (dialog != null && dialog.isShowing()) {
            task.cancel(true);
        }*/
    }

    private void setupVisualizerFxAndUI() {
        PlayerControl playerControl = new PlayerControl(ExoPlayer.Factory.newInstance(1));

        int audioSessionId = playerControl.getAudioSessionId();
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        //Player.mVisualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    public void stop() {
        if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
            this.wifiLock.release();
            this.stopForeground(true);
        }
    }

    public boolean isPlaying() {
        return exoPlayer != null && exoPlayer.getPlayWhenReady();
    }

    public void playprevious(Explore_model_item radio) {

        RadiophonyService.station = radio;

        if (station.getName().endsWith(".m3u")) {
            uri = Uri.parse(Parser.parse(station.getName()));
        } else {
            uri = Uri.parse(station.getName());
        }

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        OkHttpClient okHttpClient = new OkHttpClient();
        //DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

        DataSource dataSource = new DefaultUriDataSource(context, null,
                new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        // audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, null, null,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);

    }

    public void playNext(Explore_model_item radio) {
        RadiophonyService.station = radio;

        if (station.getName().endsWith(".m3u")) {
            uri = Uri.parse(Parser.parse(station.getName()));
        } else {
            uri = Uri.parse(station.getName());
        }

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        OkHttpClient okHttpClient = new OkHttpClient();
        //DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

        DataSource dataSource = new DefaultUriDataSource(context, null,
                new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        // audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                MediaCodecSelector.DEFAULT, null, true, null, null,
                AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);

        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);

    }

    public Explore_model_item getPlayingRadioStation() {
        return station;
    }

    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        public ProgressTask() {
            //dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {

        }

        protected Boolean doInBackground(final String... args) {
            try {
                if (station.getName().endsWith(".m3u")) {
                    uri = Uri.parse(Parser.parse(station.getName()));
                } else {
                    uri = Uri.parse(station.getName());
                }
                Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
                String userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
                OkHttpClient okHttpClient = new OkHttpClient();

                DataSource dataSource = new DefaultUriDataSource(context, null,
                        new OkHttpDataSource(okHttpClient, userAgent, null, null, CacheControl.FORCE_NETWORK));
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
                        BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
                MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                        MediaCodecSelector.DEFAULT, null, true, null, null,
                        AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);


                exoPlayer.prepare(audioRenderer);

                return true;
            } catch (IllegalArgumentException | SecurityException | IllegalStateException e1) {
            }
            return false;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_FULL, "RadiophonyLock");
                wifiLock.acquire();
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(true);
                    //setupVisualizerFxAndUI();
                    //  mVisualizer.setEnabled(true);
                }
            }
        }


    }

}
