# lab6-solution - wan gateway simulation using Dockers


## Lab Goals
Be able to start WAN simulation using Dockers <br />

## Lab setup
1.  Add GS_HOME system variable and point to Gigaspaces home directory: <br />
2.  Add export GS_WAN_TRAINING_HOME system variable and point to your WAN TRAINING directory: <br />

        vi ~/.bash_profile <br />
        export GS_HOME=~/XAP-Builds/gigaspaces-xap-enterprise-15.0.0
        export GS_WAN_TRAINING_HOME=~/XAPWANTraining
                               
3.  Make sure you restart gs-agent and gs-ui (or at least undeploy all Processing Units using gs-ui)
    
## 2.1	Clone the project lab

2.1.1 Create lab directory

    mkdir ~/XAPWANTraining/labs/lab6-solution
      
2.1.2 Clone the project from git
    
    cd ~/XAPWANTraining/labs/lab6-solution
    git clone https://github.com/GigaSpaces-ProfessionalServices/xap-wan-training.git 
    
2.1.3 Checkout lab6-solution
    
    cd xap-wan-training
    git checkout lab6-solution
    
2.1.4 Verify that the branch has been checked out.
    
    git branch
    * lab6-solution
      master 
      
## 2.2	Run Docker commands

2.2.1 Install Docker. <br />
2.2.2 Map US and EMEA to your localhost: <br />

    vi /etc/hosts
    127.0.0.1       localhost US EMEA
    
2.2.3 Run Docker US    

    docker run --name US --hostname=US -e GS_NIC_ADDRESS=172.17.0.2 -it -e GS_GSC_OPTIONS="-Xmx124m -Dcom.gs.zones=us_zone" -e GS_MANAGER_OPTIONS=-Xmx124m -e GS_GSA_OPTIONS=-Xmx124m -e XAP_LICENSE=tryme -p 8090:8090 -p 8099:8099 -v ~/docker/US/logs:/opt/gigaspaces/logs gigaspaces/xap-enterprise host run-agent --manager --webui --gsc=2

2.2.4 Run Docker EMEA

    docker run --name EMEA --hostname=EMEA -e GS_NIC_ADDRESS=172.17.0.3 -it -e GS_GSC_OPTIONS="-Xmx124m -Dcom.gs.zones=emea_zone" -e GS_MANAGER_OPTIONS=-Xmx124m -e GS_GSA_OPTIONS=-Xmx124m -e XAP_LICENSE=tryme -p 8091:8091 -v ~/docker/EMEA/logs:/opt/gigaspaces/logs gigaspaces/xap-enterprise host run-agent --manager --webui --gsc=2

![snapshot](Pictures/Picture1.png) <br />