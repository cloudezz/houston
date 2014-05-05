package com.cloudezz.houston.domain.cloud;

public enum MachineConfig {

  JUMBO(16342,8), LARGE(8192,6), MEDIUM(6144,4), SMALL(4096,2), TINY(2048,1),MICRO(512,1);
  
  private int ram;
  
  private int cpu;

  private MachineConfig(int ram,int cpu) {
          this.ram = ram;
          this.cpu= cpu;
  }
  
  public int getRam() {
    return ram;
  }

  public void setRam(int ram) {
    this.ram = ram;
  }

  public int getCpu() {
    return cpu;
  }

  public void setCpu(int cpu) {
    this.cpu = cpu;
  }





}
