package com.atguigu.myrule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @descriptions: My Balancer Rule
 * @author: Tom
 * @date: 2020/12/20 下午 08:32
 * @version: 1.0
 */
public class MyBalancerRule extends AbstractLoadBalancerRule {

    private int total = 0; //服务被调用次数
    private int currentIndex = 0; //当前提供服务的服务下标

    public MyBalancerRule(){

    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        return this.choose(this.getLoadBalancer(), key);
    }

    /**
     * 自定义策略，访问五次更换服务
     */
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        } else {
            Server server = null;

            while(server == null) {
                if (Thread.interrupted()) {
                    return null;
                }

                List<Server> upList = lb.getReachableServers(); //获取现存的服务
                List<Server> allList = lb.getAllServers(); //获取所有的服务,包含已经死掉的服务
                int serverCount = allList.size();
                if (serverCount == 0) { //如果不存在服务,返回
                    return null;
                }

                if(total < 5){ //服务未被调用五次，可以继续使用当前服务
                    server = upList.get(currentIndex); //获取当前服务
                    total++; //增加服务调用次数
                }else{ //服务已被调用五次，需要更换服务
                    total = 0; //归零服务被调用次数
                    currentIndex++; //下移当前被调用服务下标
                    if(currentIndex > upList.size())currentIndex = 0; //如果当前下标大于现存的服务个数，则从头开始
                    server = upList.get(currentIndex); //获取当前服务
                }

                if (server == null) {
                    Thread.yield();
                } else {
                    if (server.isAlive()) {
                        return server;
                    }
                    server = null;
                    Thread.yield();
                }
            }

            return server;
        }
    }
}
