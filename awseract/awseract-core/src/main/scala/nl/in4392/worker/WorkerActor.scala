package main.scala.nl.in4392.worker

import main.scala.nl.in4392.models.DistributedProtocol._
import main.scala.nl.in4392.models.Task._

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import java.util.UUID
import nl.tudelft.ec2interface.taskmonitor.TaskInfo
import java.sql.Timestamp

class WorkerActor(workerId: String,masterPath: ActorPath) extends Actor with ActorLogging {

  val master = context.actorSelection(masterPath)
 // val workerId = UUID.randomUUID().toString

  val system = ActorSystem("JobExecutorSystem")
  // default Actor constructor
  val jobExecutor = system.actorOf(Props[JobExecutorActor], "jobexec")


  override def preStart() = {
    println("Worker " + workerId + " starts")
       master ! WorkerRegister(workerId)

  }

  def stateIdle: Receive = {

    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      println("got a job!! ")
      var tInfo = new TaskInfo().FromJson(task.taskInfo)
      tInfo.setStartTime(new Timestamp(System.currentTimeMillis()))
      println("Job info: id {}, job {}",task.taskId, task.taskInfo.toString)
      jobExecutor ! new Task(task.taskId,task.job,new TaskInfo().ToJson(tInfo))
      context.become(stateWorking())
  }

  def stateWorking(): Receive = {

    case TaskResult(taskId,result,taskInfo) =>
      println("Task Finished. Result {}.", result)
      var tInfo = new TaskInfo().FromJson(taskInfo)
      tInfo.setFinishTime(new Timestamp(System.currentTimeMillis()))

      master ! TaskCompleted(workerId,taskId,result,new TaskInfo().ToJson(tInfo))
      master ! WorkerRequestTask(workerId)
      context.become(stateIdle)

    case _: Task =>
      log.info("Already working!")
  }



  def receive = stateIdle

}
