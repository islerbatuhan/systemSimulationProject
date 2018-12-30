package simulation;

public class Server{
    
    int number_of_customers_completed_service = 0;
    Customer last_customer;
    double event_start_time;
    double total_busy_time = 0;
    
    double customer_departure_time(){
        if(Global_Timer.current_time==0){
            return last_customer.service_time;
        }
        else{
            return last_customer.service_time + event_start_time;
        }
    }
    
    boolean isBusy(){
        if(this.customer_departure_time() <= Global_Timer.current_time) return false;
        else return true;
    }
}
