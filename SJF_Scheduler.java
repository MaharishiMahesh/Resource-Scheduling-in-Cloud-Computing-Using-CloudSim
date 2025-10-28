package mp; // Replace 'mp' with your actual package name if necessary.

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class SJF_Scheduler {

    private static List<Cloudlet> cloudletList;
    private static List<Vm> vmList;
    private static Datacenter[] datacenter;
    private static double[][] commMatrix;
    private static double[][] execMatrix;

    private static List<Vm> createVM(int userId, int vms) {
        List<Vm> list = new LinkedList<>();

        long size = 10000; // Image size (MB)
        int ram = 512;     // VM memory (MB)
        int mips = 250;
        long bw = 1000;    // Bandwidth (MB/s)
        int pesNumber = 1; // Number of CPUs
        String vmm = "Xen"; // VMM name

        for (int i = 0; i < vms; i++) {
            Vm vm = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm);
        }

        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        List<Cloudlet> list = new LinkedList<>();

        long fileSize = 300;    // File size (MB)
        long outputSize = 300;  // Output size (MB)
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < cloudlets; i++) {
            int dcId = (int) (Math.random() * Constants.NO_OF_DATA_CENTERS);
            long length = (long) (1e3 * (commMatrix[i][dcId] + execMatrix[i][dcId]));

            Cloudlet cloudlet = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize,
                    utilizationModel, utilizationModel, utilizationModel);

            cloudlet.setUserId(userId);
            cloudlet.setVmId(dcId % Constants.NO_OF_DATA_CENTERS); // Bind cloudlet to a VM in a round-robin manner
            list.add(cloudlet);
        }
        return list;
    }

    public static void main(String[] args) {
        Log.printLine("Starting SJF Scheduler...");

        // Initialize matrices
        new GenerateMatrices();
        execMatrix = GenerateMatrices.getExecMatrix();
        commMatrix = GenerateMatrices.getCommMatrix();

        try {
            // Initialize CloudSim
            int num_user = 1;   // Number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // Disable trace events
            CloudSim.init(num_user, calendar, trace_flag);

            // Create Datacenters
            datacenter = new Datacenter[Constants.NO_OF_DATA_CENTERS];
            for (int i = 0; i < Constants.NO_OF_DATA_CENTERS; i++) {
                datacenter[i] = DatacenterCreator.createDatacenter("Datacenter_" + i);
            }

            // Create Broker
            SJFDatacenterBroker broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            // Create VMs and Cloudlets
            vmList = createVM(brokerId, Constants.NO_OF_DATA_CENTERS);
            cloudletList = createCloudlet(brokerId, Constants.NO_OF_TASKS, 0);

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            // Start Simulation
            CloudSim.startSimulation();

            // Retrieve results
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            // Print results
            printCloudletList(newList);

            Log.printLine(SJF_Scheduler.class.getName() + " finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error.");
        }
    }

    private static SJFDatacenterBroker createBroker(String name) throws Exception {
        return new SJFDatacenterBroker(name);
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        String indent = "    ";
        DecimalFormat dft = new DecimalFormat("###.##");

        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" +
                indent + "Data center ID" +
                indent + "VM ID" +
                indent + "Time" +
                indent + "Start Time" +
                indent + "Finish Time" +
                indent + "Waiting Time");

        for (Cloudlet cloudlet : list) {
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() +
                        indent + indent + indent+"\t" + cloudlet.getVmId() +
                        indent + indent + indent+ "\t"+dft.format(cloudlet.getActualCPUTime()) +
                        indent + indent + indent+ "\t"+dft.format(cloudlet.getExecStartTime()) +
                        indent + indent + indent + "\t"+dft.format(cloudlet.getFinishTime()) +
                        indent + indent + indent+ "\t"+dft.format(cloudlet.getWaitingTime()));
            }
        }

        double makespan = calcMakespan(list);
        Log.printLine("Makespan using SJF: " + dft.format(makespan));
    }

    private static double calcMakespan(List<Cloudlet> list) {
        double makespan = 0;
        double[] dcWorkingTime = new double[Constants.NO_OF_DATA_CENTERS];

        for (Cloudlet cloudlet : list) {
            int dcId = cloudlet.getVmId() % Constants.NO_OF_DATA_CENTERS;
            if (dcWorkingTime[dcId] > 0) dcWorkingTime[dcId] -= 1;
            dcWorkingTime[dcId] += execMatrix[cloudlet.getCloudletId()][dcId] +
                    commMatrix[cloudlet.getCloudletId()][dcId];
            makespan = Math.max(makespan, dcWorkingTime[dcId]);
        }
        return makespan;
    }
}
