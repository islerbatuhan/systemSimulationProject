package simulation;

import static java.lang.Math.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class MMc_no_break {
    
    public static void main(String[] args) {
        
        int total_simulation_time=1000000;
        double mean_arrival_rate=50;
        double mean_service_time=300;
        int number_of_customers=1000000;
        
        double total_of_interarrival = 0;
        double total_waiting_time=0;
        int number_of_customers_waited = 0;
        Customer current_customer;
        double total_area_under_queue = 0;
        
        Queue<Customer> customer_pool = new LinkedList<>();
        LinkedList<Customer> waiting_queue = new LinkedList<>();
        
        for(int i=1;i<=number_of_customers;i++){ //creates a queue of potential customers called customer_pool
            Customer customer = new Customer();
            customer.interarrival_time = Poisson(mean_arrival_rate);
            total_of_interarrival += customer.interarrival_time;
            customer.service_time = exponential(mean_service_time);
            if(i!=1)    customer.arrival_time += total_of_interarrival;
            customer_pool.add(customer);
        }
        
        Server server1 = new Server();
        Server server2 = new Server();
        
        while(Global_Timer.current_time<=total_simulation_time){
            
            if(Global_Timer.current_time==0){ //gets the first customer in the customer pool into the server1
                current_customer = customer_pool.poll();
                server1.last_customer = current_customer;
                server1.event_start_time = 0;
                server1.total_busy_time += current_customer.service_time;
                server1.number_of_customers_completed_service++;
            }
            else{
                
                if(waiting_queue.isEmpty()){
                    
                    if(!server1.isBusy()){
                        current_customer = customer_pool.poll();
                        server1.event_start_time = current_customer.arrival_time;                            
                        server1.last_customer = current_customer;
                        server1.total_busy_time += current_customer.service_time;
                        server1.number_of_customers_completed_service++;
                    }
                    
                    else if(server2.number_of_customers_completed_service==0){
                        current_customer = customer_pool.poll();
                        server2.event_start_time = current_customer.arrival_time;
                        server2.last_customer = current_customer; 
                        server2.total_busy_time += current_customer.service_time;
                        server2.number_of_customers_completed_service++;
                    }
                    
                    else if(!server2.isBusy()){
                        current_customer = customer_pool.poll();
                        server2.event_start_time = current_customer.arrival_time;                            
                        server2.last_customer = current_customer;
                        server2.total_busy_time += current_customer.service_time;
                        server2.number_of_customers_completed_service++;
                    }
                }
                
                else if (!waiting_queue.isEmpty()){
                    
                    if(!server1.isBusy()){
                        current_customer = waiting_queue.poll();
                        server1.event_start_time = Global_Timer.current_time;
                        number_of_customers_waited++;
                        total_waiting_time += Global_Timer.current_time - current_customer.arrival_time;
                        server1.last_customer = current_customer;
                        server1.total_busy_time += current_customer.service_time;
                        server1.number_of_customers_completed_service++;
                    }
                    
                    else if(!server2.isBusy()){
                        current_customer = waiting_queue.poll();
                        server2.event_start_time = Global_Timer.current_time; 
                        number_of_customers_waited++;
                        total_waiting_time += Global_Timer.current_time - current_customer.arrival_time;
                        server2.last_customer = current_customer;
                        server2.total_busy_time += current_customer.service_time;
                        server2.number_of_customers_completed_service++;
                    }   
                }
                
                while(customer_pool.element().arrival_time <= Global_Timer.current_time){
                    if(!waiting_queue.isEmpty())    total_area_under_queue += (waiting_queue.size() + 1) * ((customer_pool.element().arrival_time) - (waiting_queue.getLast().arrival_time));    
                    waiting_queue.add(customer_pool.poll());        
                }        
            }
                
            if (waiting_queue.isEmpty())    Global_Timer.current_time = customer_pool.element().arrival_time;
            else{
                if (server2.customer_departure_time() < server1.customer_departure_time())  Global_Timer.current_time = server2.customer_departure_time();
                else Global_Timer.current_time = server1.customer_departure_time();
            }  
                
        }
        
        if(server1.customer_departure_time() > server2.customer_departure_time())   Global_Timer.current_time=server1.customer_departure_time();
        else    Global_Timer.current_time=server2.customer_departure_time();
        
        if(Global_Timer.current_time < total_simulation_time) Global_Timer.current_time=total_simulation_time;
        
        System.out.println("Total run time= " + Global_Timer.current_time);
        System.out.println("Average waiting time = " + (total_waiting_time)/(server1.number_of_customers_completed_service + server2.number_of_customers_completed_service));
        System.out.println("Average waiting time of those who wait = " + total_waiting_time/number_of_customers_waited);
        System.out.println("Total busy time of Server 1 = " + server1.total_busy_time);
        System.out.println("Total idle time of Server 1 = " + (Global_Timer.current_time - server1.total_busy_time));
        System.out.println("Total busy time of Server 2 = " + server2.total_busy_time);
        System.out.println("Total idle time of Server 2 = " + (Global_Timer.current_time - server2.total_busy_time));     
        System.out.println("Idle state probabilty of Server 1 = " + (Global_Timer.current_time - server1.total_busy_time) / ((Global_Timer.current_time - server1.total_busy_time) + server1.total_busy_time));
        System.out.println("Busy state probabilty of Server 1 = " + (server1.total_busy_time) / ((Global_Timer.current_time - server1.total_busy_time) + server1.total_busy_time));
        System.out.println("Idle state probabilty of Server 2 = " + (Global_Timer.current_time - server2.total_busy_time) / ((Global_Timer.current_time - server2.total_busy_time) + server2.total_busy_time));
        System.out.println("Busy state probabilty of Server 2 = " + (server2.total_busy_time) / ((Global_Timer.current_time - server2.total_busy_time) + server2.total_busy_time));
        System.out.println("Idle state probabilty of System = " + ((Global_Timer.current_time - server1.total_busy_time) + (Global_Timer.current_time - server2.total_busy_time)) / (((server1.total_busy_time)+(server2.total_busy_time)) + (Global_Timer.current_time - server1.total_busy_time) + (Global_Timer.current_time - server2.total_busy_time)));
        System.out.println("Busy state probabilty of System = " +(((server1.total_busy_time)+(server2.total_busy_time))) / (((server1.total_busy_time)+(server2.total_busy_time)) + (Global_Timer.current_time - server1.total_busy_time) + (Global_Timer.current_time - server2.total_busy_time)));
        System.out.println("Utilisation of the system = " +(((server1.total_busy_time)+(server2.total_busy_time))) / (((server1.total_busy_time)+(server2.total_busy_time)) + (Global_Timer.current_time - server1.total_busy_time) + (Global_Timer.current_time - server2.total_busy_time)));
        System.out.println("Mean queue length = " + total_area_under_queue / Global_Timer.current_time);
        System.out.println("Throughput = " + (server1.number_of_customers_completed_service + server2.number_of_customers_completed_service) / Global_Timer.current_time);
        System.out.println("Total number of customers completed their job in System = " + (server1.number_of_customers_completed_service + server2.number_of_customers_completed_service));    
        System.out.println("Total number of customers completed their job in Server 1 = " + server1.number_of_customers_completed_service);
        System.out.println("Total number of customers completed their job in Server 2 = " + server2.number_of_customers_completed_service);
        System.out.println("Average response time = " + (total_waiting_time) / (server1.number_of_customers_completed_service + server2.number_of_customers_completed_service));    

    }
    
    private static double Poisson(double mean) {
        Random r = new Random();
        int i = 0;
        double L = Math.exp(-mean);
        double d = 1;
        do {
            d = d * r.nextDouble();
            i++;
        }while (d > L);
        return i - 1;
    }
    
    private static double exponential(double m){
	double  r;
        do{
        r = Math.random();
        }while(r==0.0 || r==1.0);
        return -log(r)*m;
    }
    
}