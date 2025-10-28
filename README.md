# SJF-Based Cloud Scheduling Simulator

This project simulates Shortest Job First (SJF) scheduling in a cloud data center environment using CloudSim. It demonstrates how tasks (cloudlets) are scheduled on virtual machines (VMs) in different data centers based on SJF principles. The project also generates communication and execution time matrices for clouds.

## Features

- Simulates cloud task (cloudlet) scheduling using SJF algorithm
- Models multiple data centers and virtual machines
- Generates and uses random communication and execution time matrices
- Shows how cloudlets are allocated, executed, and results reported
- Modular code for creating data centers, brokers, VMs, and tasks

## Project Structure

| File                      | Purpose                                                                     |
|---------------------------|-----------------------------------------------------------------------------|
| `SJF_Scheduler.java`      | Main simulation entry point; runs SJF scheduling and outputs results        |
| `MiniProject.java`        | Handles generation and initialization of matrices (communication & execution times) |
| `DatacenterCreator.java`  | Utility to create and configure data centers in CloudSim                    |
| `SJFDatacenterBroker.java`| Custom broker that allocates cloudlets to VMs using SJF algorithm           |
| `Constants.java`          | Contains project constants (number of tasks, datacenters, etc.)             |
| `GenerateMatrices.java`   | Helper to generate/read matrices for task scheduling                        |

## Getting Started

1. Clone or download this repository.
2. Make sure you have [CloudSim](http://www.cloudbus.org/cloudsim/) libraries available and added to your classpath.
3. Compile all `.java` files in your Java IDE (e.g. Eclipse) or use the command line.
4. Run `SJF_Scheduler.java` to start the simulation.

## Usage

To run the simulation, execute:

javac *.java
java SJF_Scheduler


Check the output in your console to see the scheduling results, including cloudlet allocation, execution times, and overall makespan.

## Requirements

- Java 8 or later
- CloudSim library (add to your classpath)

## License

This project is for educational use and academic demonstration purposes.
