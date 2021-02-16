package code.audio;

import code.math.Vector3D;

import code.utils.assetManager.AssetManager;
import code.utils.assetManager.DisposableContent;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

public class SoundSource extends DisposableContent {
    public static final float defRefDist = 1, defMaxDist = 1000;

    public String soundName;
    public SoundBuffer buffer;
    
    private int soundSource;
    private static final float[] sourcePos = new float[3], sourceSpeed = new float[3];
    private boolean use3D = true;
    
    /**
     * Should be destoroyed after using!
     */
    public SoundSource() {
        init();
    }
    
    /**
     * Should be destoroyed after using!
     */
    public SoundSource(String file) {
        init();
        loadFile(file);
    }
    
    public static SoundSource get() {
        return get(null);
    }
    
    public static SoundSource get(String file) {
        SoundSource source = file==null?new SoundSource():new SoundSource(file);
        AssetManager.addDisposable(source);
        return source;
    }
    
    private void init() {
        // Bind the buffer with the source.
        soundSource = AL10.alGenSources();
        
        sourcePos[0] = sourcePos[1] = sourcePos[2] = 0;
        AL10.alSourcefv(soundSource, AL10.AL_POSITION, sourcePos);
        sourceSpeed[0] = sourceSpeed[1] = sourceSpeed[2] = 0;
        AL10.alSourcefv(soundSource, AL10.AL_VELOCITY, sourceSpeed);
        
        //AL10.alSourcef(soundSource, AL10.AL_ROLLOFF_FACTOR, 0.01f);
        //AL10.alSourcef(soundSource, AL10.AL_ROLLOFF_FACTOR, 0.4f);
        
        setDistance(defRefDist, defMaxDist);
        
        /*if(AudioEngine.auxEffectSlot != 0) {
            AL11.alSource3i(soundSource, EXTEfx.AL_AUXILIARY_SEND_FILTER, 
                    AudioEngine.auxEffectSlot, 0, EXTEfx.AL_FILTER_NULL);
        }*/
    }
    
    public SoundSource beMusicPlayer() {
        //player.setVolume(musicGain); //todo
        setLoop(true);
        set3D(false);
        
        /*if(AudioEngine.auxEffectSlot != 0) {
            AL11.alSource3i(soundSource, EXTEfx.AL_AUXILIARY_SEND_FILTER, 
                    EXTEfx.AL_EFFECTSLOT_NULL, 0, EXTEfx.AL_FILTER_NULL);
        }*/
        
        return this;
    }

    public void loadFile(String file) {
        soundName = null;
        
        buffer = SoundBuffer.get(file);
        if(buffer != null) {
            AL10.alSourcei(soundSource, AL10.AL_BUFFER, buffer.id);
            soundName = file;
        } else AL10.alSourcei(soundSource, AL10.AL_BUFFER, 0);
    }

    public void setLoop(boolean loop) {
        AL10.alSourcei(soundSource, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public boolean getLoop() {
        return AL10.alGetSourcei(soundSource, AL10.AL_LOOPING) == AL10.AL_TRUE;
    }

    public void setVolume(float gain) {
        AL10.alSourcef(soundSource, AL10.AL_GAIN, gain);
    }

    public float getVolume() {
        return AL10.alGetSourcef(soundSource, AL10.AL_GAIN);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(soundSource, AL10.AL_PITCH, pitch);
    }

    public float getPitch() {
        return AL10.alGetSourcef(soundSource, AL10.AL_PITCH);
    }
    
    public void setPosition(Vector3D pos) {
        if(!use3D) return;
        
        sourcePos[0] = pos.x; sourcePos[1] = pos.y; sourcePos[2] = pos.z;
        AL10.alSourcefv(soundSource, AL10.AL_POSITION, sourcePos);
    }
    
    public void setSpeed(Vector3D speed) {
        if(!use3D) return;
        
        sourceSpeed[0] = speed.x; sourceSpeed[1] = speed.y; sourceSpeed[2] = speed.z;
        AL10.alSourcefv(soundSource, AL10.AL_VELOCITY, sourceSpeed);
    }
    
    public void setDistance(float reference, float max) {
        AL10.alSourcef(soundSource, AL10.AL_REFERENCE_DISTANCE, reference);
        AL10.alSourcef(soundSource, AL10.AL_MAX_DISTANCE, max);
        //AL10.alSourcef(soundSource, AL10.AL_REFERENCE_DISTANCE, 100);
    }
    
    public void set3D(boolean use3D) {
        this.use3D = use3D;
        AL10.alSourcei(soundSource, AL10.AL_SOURCE_RELATIVE, use3D?AL10.AL_FALSE:AL10.AL_TRUE);
        
        if(!use3D) {
            sourcePos[0] = sourcePos[1] = sourcePos[2] = 0;
            AL10.alSourcefv(soundSource, AL10.AL_POSITION, sourcePos);
            sourceSpeed[0] = sourceSpeed[1] = sourceSpeed[2] = 0;
            AL10.alSourcefv(soundSource, AL10.AL_VELOCITY, sourceSpeed);
        }
    }

    public void play() {
        AL10.alSourcePlay(soundSource);
    }

    public void pause() {
        AL10.alSourcePause(soundSource);
    }

    public void stop() {
        AL10.alSourceStop(soundSource);
    }
    
    public boolean isPlaying() {
        int[] out = new int[1];
        AL10.alGetSourcei(soundSource, AL10.AL_SOURCE_STATE, out);
        return out[0] == AL10.AL_PLAYING;
    }
    
    public void rewind() {
        AL10.alSourceRewind(soundSource);
        AL10.alSourcePlay(soundSource);
    }
    
    public void destroy() {
        AL10.alDeleteSources(soundSource);
    }
    
    public void free() {
        if(buffer != null) buffer.free();
        AL10.alSourcei(soundSource, AL10.AL_BUFFER, 0);
        buffer = null;
    }

    public int getID() {
        return soundSource;
    }
}