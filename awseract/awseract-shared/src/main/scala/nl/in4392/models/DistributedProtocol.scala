package main.scala.nl.in4392.models

import nl.tudelft.ec2interface.taskmonitor._
object DistributedProtocol {


  case class WorkerRegister(workerId: String)
  case class WorkerRequestTask(workerId: String)

  case class TaskCompleted(workerId: String, taskId: String, result: Any, taskInfo: TaskInfo)
  case class TaskFailed(workerId: String, taskId: String, taskInfo: TaskInfo)

  case object TaskAvailable

  //Monitoring
  case object RequestSystemInfo
  case class ReportSystemInfo(workerId:String,jsonString:String)
  case class MonitorRegister(workerId: String)
}
