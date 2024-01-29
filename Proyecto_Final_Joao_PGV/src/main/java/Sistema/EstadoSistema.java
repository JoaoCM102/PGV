/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Sistema;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import oshi.SystemInfo;
import oshi.hardware.Display;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.LogicalVolumeGroup;
import oshi.hardware.UsbDevice;
import oshi.hardware.common.AbstractHardwareAbstractionLayer;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;

/**
 *
 * @author joaop
 */
public class EstadoSistema implements Serializable, Runnable {

    public SystemInfo estadoSistema = new SystemInfo();
    public HardwareAbstractionLayer hardware = estadoSistema.getHardware();
    public OperatingSystem sistema = estadoSistema.getOperatingSystem();

    public List<OSProcess> procesos;
    public int numeroProcesos;
    List<OSService> serviciosList = sistema.getServices();
    public int numeroHilos;
    public long tiempoSistema;
    public OperatingSystem.OSVersionInfo version = sistema.getVersionInfo();
    public String fabricante = sistema.getManufacturer();
    private InternetProtocolStats estadisticasProtocoloInternet;//explorar usos
    public List<Display> pantallas = hardware.getDisplays();
    private List<UsbDevice> dispositivosUsb;//explorar usos
    public List<GraphicsCard> graficas = hardware.getGraphicsCards();

    public void actualizar() {
        ExecutorService ejecutor = Executors.newFixedThreadPool(1);
        ejecutor.execute(() -> {
            numeroHilos = sistema.getThreadCount();
            tiempoSistema = sistema.getSystemUptime();
            procesos = sistema.getProcesses();
            numeroProcesos = sistema.getProcessCount();
            serviciosList = sistema.getServices();
            
        });
    }
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        // Escribir objetos que no son serializables por defecto
        oos.writeObject(estadisticasProtocoloInternet);
        oos.writeObject(procesos);
        oos.writeInt(numeroProcesos);
        oos.writeInt(numeroHilos);
        oos.writeLong(tiempoSistema);
        oos.writeObject(serviciosList);
        oos.writeObject(pantallas);
        oos.writeObject(dispositivosUsb);
        oos.writeObject(graficas);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        // Leer objetos que no son serializables por defecto
        estadisticasProtocoloInternet = (InternetProtocolStats) ois.readObject();
        procesos = (List<OSProcess>) ois.readObject();
        numeroProcesos = ois.readInt();
        numeroHilos = ois.readInt();
        tiempoSistema = ois.readLong();
        serviciosList = (List<OSService>) ois.readObject();
        pantallas = (List<Display>) ois.readObject();
        dispositivosUsb = (List<UsbDevice>) ois.readObject();
        graficas = (List<GraphicsCard>) ois.readObject();
    }

    @Override
    public void run() {
        actualizar();
    }
}
