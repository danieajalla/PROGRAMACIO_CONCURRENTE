package practica;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class SystemInfo {
    public static void main(String[] args) {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        System.out.println("CPU Load: " + osBean.getSystemCpuLoad() * 100 + "%");
        System.out.println("Total Physical Memory: " + osBean.getTotalPhysicalMemorySize() / (1024 * 1024) + " MB");
        System.out.println("Free Physical Memory: " + osBean.getFreePhysicalMemorySize() / (1024 * 1024) + " MB");
    }
}

