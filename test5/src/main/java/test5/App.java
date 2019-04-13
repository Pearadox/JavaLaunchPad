package test5;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.concurrent.CountDownLatch;  

/**
 * Hello world!
 *
 */
public class App 
{
    public static final String DEVICE_SIGNATURE = "Launchpad MK2";
    private static CountDownLatch stop = new CountDownLatch(1);

    public static void main( String[] args )
    {
      System.out.println("Launchpad App");
    try
    {
    MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
      for (MidiDevice.Info info : midiDeviceInfo) {
          System.out.println(info.getName());
          if (info.getDescription().contains(DEVICE_SIGNATURE) || info.getName().contains(DEVICE_SIGNATURE)) {
              MidiDevice device = MidiSystem.getMidiDevice(info);
              if (device.getMaxTransmitters() == -1) {
                  System.out.println("Device info:" + info.getDescription());
                  System.out.println(DEVICE_SIGNATURE + " detected");
              }
              device.close();
          }
      }
    }
    catch(MidiUnavailableException e)
    {
      System.out.println("No device found");
    }
    //
    try
    {
      Launchpad launchpad = new MidiLaunchpad(MidiDeviceConfiguration.autodetect());

      LaunchpadClient client = launchpad.getClient();

      launchpad.setListener(new MyListener(client));
      client.reset();
      client.setButtonLight(Button.STOP, Color.RED, BackBufferOperation.NONE);
      client.testLights(LightIntensity.MEDIUM);
      stop.await();
      System.out.println("Launchpad closed");
      client.reset();
      launchpad.close();
    }
    catch(MidiUnavailableException e)
    {
      System.out.println("No device found");
    }
    catch(Exception e)
    {
      System.out.println("Couldn't close launchpad");
    }
  }

  public static class MyListener extends LaunchpadListenerAdapter {

    private final LaunchpadClient client;
    private boolean toggle = false;

        public MyListener(LaunchpadClient client) {
            this.client = client;
        }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {
        System.out.println("Pad pressed : "+pad);
        if(toggle)
        {
          client.setPadLight(pad, Color.BLUE, BackBufferOperation.NONE);
        }
        else
        {
          //GINA - can you see this?
          client.setPadLight(pad, Color.GREEN, BackBufferOperation.NONE);
                  }
        toggle = !toggle;
        System.out.println(toggle);
    }

}
}
