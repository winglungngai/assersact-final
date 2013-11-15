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
import akka.util.Timeout
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import akka.pattern.{ ask, pipe }
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{ Success, Failure }
import nl.tudelft.ec2interface.instancemanager._

class WorkerActor(workerId: String,masterPath: ActorPath) extends Actor with ActorLogging {

  val instanceId = new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getSelfInstanceID

  val master = context.actorSelection(masterPath)
 // val workerId = UUID.randomUUID().toString

  val system = ActorSystem("JobExecutorSystem")
  // default Actor constructor
  val jobExecutor = system.actorOf(Props[JobExecutorActor], "jobexec")


  override def preStart() = {
    println("Worker " + workerId + " starts")
       master ! WorkerRegister(workerId)

  }


  def receive = {
    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      var tInfo = new TaskInfo().FromJson(task.taskInfo)
      tInfo.setWorkerId(instanceId)
      tInfo.setStartTime(new Timestamp(System.currentTimeMillis()))
      println("Job info: id {}, job {}",task.taskId, task.taskInfo.toString)
      implicit val timeout = Timeout(10 seconds)
      val resultFuture = jobExecutor ? new Task(task.taskId,task.job,new TaskInfo().ToJson(tInfo))
      resultFuture onComplete {
        case Success(TaskResult(taskId,result,taskInfo)) =>
          println("Task Finished. Result {}.", result)
          var tInfo = new TaskInfo().FromJson(taskInfo)
          tInfo.setFinishTime(new Timestamp(System.currentTimeMillis()))
          master ! TaskCompleted(workerId,taskId,result,new TaskInfo().ToJson(tInfo))
          master ! WorkerRequestTask(workerId)
        case Failure(t) =>
          t.printStackTrace()
      }
  }

 /*

  def stateIdle: Receive = {

    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      println("got a job!! ")
      var tInfo = new TaskInfo().FromJson(task.taskInfo)
      tInfo.setWorkerId(instanceId)
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
  */

}
