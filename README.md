# Hardware Engineering Yard (HEY)

## Overview

Welcome to Hardware Engineering Yard (HEY), a comprehensive repository aimed at providing a collection of simple IPs developed in SystemVerilog and Chisel. I hope this repository becomes a textbook on how to use SystemVerilog or Chisel.

## Repository Structure

The HEY repository is organized into several key directories, each hosting specific types of content:

-   `rtl_codes/`: Contains SystemVerilog code for various IPs. This directory is further divided into:
    -   `commons/`: basic IPs such as RRArbiter and FIFO.
    -   `bus_model/`: Contains VIP models(AXI/Tilelink) written in SystemVerilog.
-   `sim/verilator`: Includes Verilator simulation scripts and environment setups for testing and verification.
-   `src/`: A Scala project using sbt as a build tool. 
    -   `src/main/scala`: Contains Scala (Chisel) sources for commons, RISC-V implementation, and bus tests.
    -   `src/test/scala`: Hosts unit tests for the Scala (Chisel) components.

## Installation

To set up the HEY project for development and testing, follow these steps:

1.  **Clone the Repository:**
    
```shell
git clone https://github.com/astrohan/Hey.git
cd hey
```
    
2.  **Set Up the Scala Project:** The Scala project requires a specific setup to integrate with RocketChip and CDE. Run the `build-setup.sh` script to automate this process:
    
```shell
./build-setup.sh
```
This script clones the RocketChip and CDE repositories into a `generator/` directory within the project and checks out the desired versions. Ensure you have git installed and accessible from your command line.
