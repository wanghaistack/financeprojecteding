package com.atguigu.atcrowdfunding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringCloudProcessFrameworkApplicationTests {
	@Autowired
	private ProcessEngine processEngine;
	// 用于流程部署
	@Autowired
	private RepositoryService repositoryService;
	// 用于创建启动流程实例
	@Autowired
	private RuntimeService runtimeService;
	// 用于查询任务，完成任务
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;

	// 1.流程部署
	/*
	 * ACT_RE_*: ‘RE’表示repository(存储)，RepositoryService接口所操作的表。
	 * 带此前缀的表包含的是静态信息，如，流程定义，流程的资源（图片，规则等）。 deployment：部署 deploy :配置、部署、展开
	 * repository：资源库
	 */
	@Test
	public void test() {
		/*
		 * Deployment deploy = repositoryService.createDeployment().
		 * addClasspathResource("MyProcess.bpmn").deploy();
		 */
		// "processes/MyProcess.bpmn"
		Deployment deploy = repositoryService.createDeployment().addClasspathResource("authflow.bpmn").deploy();
		System.out.println(deploy);
	}

	@Test
	public void contextLoads() {
		System.out.println(processEngine);
		System.out.println(repositoryService);
	}

	// 2.流程查询
	@Test
	public void test2() {
		// 创建流程定义查询
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		// 获取查询数据的集合 act_re_procdef表中的数据
		List<ProcessDefinition> list = processDefinitionQuery.list();
		// 迭代
		for (ProcessDefinition processDefinition : list) {
			String key = processDefinition.getKey();
			String id = processDefinition.getId();
			String resourceName = processDefinition.getResourceName();
			int version = processDefinition.getVersion();
			System.out.println("********************" + "key**" + key + "id**" + id + "resourceName**" + resourceName
					+ "version**" + version);
		}
		long count = processDefinitionQuery.count();
		ProcessDefinition singleResult = processDefinitionQuery.latestVersion().singleResult();// 获取最后一次部署的流程定义对象
		System.out.println("******" + singleResult);
		ProcessDefinitionQuery desc = processDefinitionQuery.orderByProcessDefinitionVersion().desc();// 版本号倒叙
		System.out.println("***" + desc);
		// 用于分页的方法
		List<ProcessDefinition> listPage = processDefinitionQuery.listPage(1, 2);// 分页的方法
		for (ProcessDefinition processDefinition : listPage) {
			System.out.println("processDefinition***" + processDefinition.getVersion());
		}
	}

	// 3.创建启动流程实例
	@Test
	public void test3() {
		// 查询流程定义
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		System.out.println(singleResult.getVersion());
		// 运行启动流程实例
		/*
		 * ProcessInstance[201] act_hi_procinst : 历史流程实例表 保存了流程实例的信息 act_hi_taskinst :
		 * 历史任务实例表 保存了流程任务的相关信息 act_hi_actinst:历史节点表 保存了流程执行的节点顺序 act_ru_execution :
		 * 运行时流程执行实例表 保存了当前流程执行的节点数据,流程开始会自动完成,直接执行第一个任务 act_ru_task : 运行时任务节点表
		 * 保存当前流程执行的任务数据
		 */
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(singleResult.getId());

		System.out.println(processInstance);
	}

	// 查询分配的任务
	@Test
	public void test4() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		List<Task> list2 = taskQuery.taskAssignee("lisi").list();
		for (Task task : list) {
			System.out.println(task.getAssignee());
			System.out.println("张三的任务是：" + task.getName());
		}
		for (Task task : list2) {
			System.out.println("李四的任务是:" + task.getName());
		}

	}

	// 查询zhangsan审批并完成任务
	@Test
	public void test5() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 查询张三的任务
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		for (Task task : list) {
			// 执行完成张三所需要完成的任务
			taskService.complete(task.getId());
		}
	}

	// 查询并完成李四的任务
	@Test
	public void test6() {
		// 创建查询服务
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 获取李四的任务
		List<Task> list = taskQuery.taskAssignee("lisi").list();
		for (Task task : list) {
			// 通过李四的id完成李四的任务
			taskService.complete(task.getId());
		}
	}

	// 查询历史完成的流程
	@Test
	public void test7() {
		// 获取历史流程实例查询对象
		HistoricProcessInstanceQuery processInstanceQuery = historyService.createHistoricProcessInstanceQuery();
		// 根据流程key查询
		List<HistoricProcessInstance> list = processInstanceQuery.processDefinitionKey("myProcess").list();
		for (HistoricProcessInstance historicProcessInstance : list) {
			System.out.println(historicProcessInstance.getName());
			System.out.println("流程开始时间" + historicProcessInstance.getStartTime());
			System.out.println("流程结束时间" + historicProcessInstance.getEndTime());
		}
	}

	// 领取任务
	@Test
	public void test8() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 查询张三有没有任务
		// 查询小组有多少任务
		List<Task> list = taskQuery.taskCandidateGroup("tl").list();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("张三的任务为：" + count);
		for (Task task : list) {
			// 张三领取任务
			taskService.claim(task.getId(), "zhangsan");
		}
		// 再次启动任务查询
		taskQuery = taskService.createTaskQuery();
		count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println(count);

	}

	@Test
	public void testTaskClaim() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<Task> list = taskQuery.taskCandidateGroup("tl").list();

		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("zhangsan领取之前的任务数量：" + count);

		for (Task task : list) {
			taskService.claim(task.getId(), "zhangsan"); // 领取任务
		}

		taskQuery = taskService.createTaskQuery();
		count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("zhangsan领取之后的任务数量：" + count);
	}

	// 流程变量
	@Test
	public void test9() {
		// 查询流程定义
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		System.out.println(singleResult.getVersion());
		// 运行启动流程实例
		/*
		 * ProcessInstance[201] act_hi_procinst : 历史流程实例表 保存了流程实例的信息 act_hi_taskinst :
		 * 历史任务实例表 保存了流程任务的相关信息 act_hi_actinst:历史节点表 保存了流程执行的节点顺序 act_ru_execution :
		 * 运行时流程执行实例表 保存了当前流程执行的节点数据,流程开始会自动完成,直接执行第一个任务 act_ru_task : 运行时任务节点表
		 * 保存当前流程执行的任务数据
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tl", "zhangsan");// 启动流程实例时传递流程变量
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(singleResult.getId(), map);

		System.out.println(processInstance);
	}

	// 在完成任务时为将要执行的节点传递流程变量
	@Test
	public void test10() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		for (Task task : list) {
			Map<String, Object> map = new HashMap<>();
			map.put("pm", "lisi");
			taskService.complete(task.getId(), map);
		}
		taskQuery = taskService.createTaskQuery();
		List<Task> list2 = taskQuery.taskAssignee("lisi").list();
		for (Task task : list2) {
			taskService.complete(task.getId());
		}

	}

	// 排他网关
	@Test
	public void test11() {
		// 流程部署
		repositoryService.createDeployment().addClasspathResource("MyProcess5.bpmn").deploy();
		// 查询流程定义
		/*
		 * ProcessDefinition singleResult =
		 * repositoryService.createProcessDefinitionQuery()
		 * .processDefinitionKey("myProcess").latestVersion().singleResult();
		 */
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		// 启动流程实例
		runtimeService.startProcessInstanceById(singleResult.getId());
	}

	@Test
	public void test12() {
		// 领取任务，执行任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 查询张三的所有任务
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		// 遍历任务，完成任务
		for (Task task : list) {
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("days", 5);
			taskService.complete(task.getId(), variables);
		}
		System.out.println("张三完成了任务");
		taskQuery = taskService.createTaskQuery();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("张三完成的任务" + count);
	}

	@Test
	public void test13() {
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 查询李四的任务数量
		long count = taskQuery.taskAssignee("lisi").count();
		System.out.println("李四的任务为：" + count);
		// 查询李四的所有任务
		List<Task> list = taskQuery.taskAssignee("lisi").list();
		for (Task task : list) {
			// 完成任务
			taskService.complete(task.getId());
		}
		System.out.println("李四完成了任务");

	}

	@Test
	public void test14() {
		// 流程部署
		repositoryService.createDeployment().addClasspathResource("MyProcess5.bpmn").deploy();
		// 查询流程定义
		/*
		 * ProcessDefinition singleResult =
		 * repositoryService.createProcessDefinitionQuery()
		 * .processDefinitionKey("myProcess").latestVersion().singleResult();
		 */
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		// 启动流程实例
		runtimeService.startProcessInstanceById(singleResult.getId());
		// 领取任务，执行任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		// 查询张三的所有任务
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		// 遍历任务，完成任务
		for (Task task : list) {
			// 张三完成任务
			taskService.complete(task.getId());
		}
		System.out.println("张三完成了任务");
		taskQuery = taskService.createTaskQuery();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("张三完成的任务" + count);
		taskQuery = taskService.createTaskQuery();
		// 查询李四的任务数量
		count = taskQuery.taskAssignee("lisi").count();
		System.out.println("李四的任务为：" + count);
		// 查询李四的所有任务
		List<Task> list1 = taskQuery.taskAssignee("lisi").list();
		for (Task task : list1) {
			// 完成任务
			taskService.complete(task.getId());
		}
		System.out.println("李四完成了任务");
	}

	// 包含网关：条件满足两条可走并行网关
	@Test
	public void test15() {
		// 流程部署
		repositoryService.createDeployment().addClasspathResource("MyProcess6.bpmn").deploy();
		// 查询流程定义
		/*
		 * ProcessDefinition singleResult =
		 * repositoryService.createProcessDefinitionQuery()
		 * .processDefinitionKey("myProcess").latestVersion().singleResult();
		 */
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		// 启动流程实例
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 5);
		variables.put("cost", 6000);
		runtimeService.startProcessInstanceById(singleResult.getId(), variables);
		// 领取任务，执行任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("张三完成的任务" + count);
		// 查询张三的所有任务
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		// 遍历任务，完成任务
		for (Task task : list) {
			// 张三完成任务
			taskService.complete(task.getId());
		}
		System.out.println("张三完成了任务");
		taskQuery = taskService.createTaskQuery();

		taskQuery = taskService.createTaskQuery();
		// 查询李四的任务数量
		count = taskQuery.taskAssignee("lisi").count();
		System.out.println("李四的任务为：" + count);
		// 查询李四的所有任务
		List<Task> list1 = taskQuery.taskAssignee("lisi").list();
		for (Task task : list1) {
			// 完成任务
			taskService.complete(task.getId());
		}
		System.out.println("李四完成了任务");
	}

	// 包含网关：条件满足一条可走排他网关
	@Test
	public void test16() {
		// 流程部署
		repositoryService.createDeployment().addClasspathResource("MyProcess6.bpmn").deploy();
		// 查询流程定义
		/*
		 * ProcessDefinition singleResult =
		 * repositoryService.createProcessDefinitionQuery()
		 * .processDefinitionKey("myProcess").latestVersion().singleResult();
		 */
		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		// 启动流程实例
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("days", 5);
		variables.put("cost", 3000);
		runtimeService.startProcessInstanceById(singleResult.getId(), variables);
		// 领取任务，执行任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println("张三完成的任务" + count);
		// 查询张三的所有任务
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		// 遍历任务，完成任务
		for (Task task : list) {
			// 张三完成任务
			taskService.complete(task.getId());
		}
		System.out.println("张三完成了任务");
		taskQuery = taskService.createTaskQuery();

		taskQuery = taskService.createTaskQuery();
		// 查询李四的任务数量
		count = taskQuery.taskAssignee("lisi").count();
		System.out.println("李四的任务为：" + count);
		// 查询李四的所有任务
		List<Task> list1 = taskQuery.taskAssignee("lisi").list();
		for (Task task : list1) {
			// 完成任务
			taskService.complete(task.getId());
		}
		System.out.println("李四完成了任务");
	}

	// 包含网关：条件满足一条可走排他网关
	@Test
	public void test17() {
		// repositoryService.createDeployment().addClasspathResource("MyProcess7.bpmn").deploy();
		// 流程部署
		repositoryService.createDeployment().addClasspathResource("MyProcess8.bpmn").deploy();
		// 查询流程定义

	/*	ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();*/

		ProcessDefinition singleResult = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("myProcess").latestVersion().singleResult();
		// 启动流程实例
		runtimeService.startProcessInstanceById(singleResult.getId());
		// 查询任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		long count = taskQuery.taskAssignee("zhangsan").count();
		System.out.println(count);
		List<Task> list = taskQuery.taskAssignee("zhangsan").list();
		for (Task task : list) {
			// 执行任务
			Map<String, Object> variables = new HashMap<String, Object>();
			variables.put("flag", false);
			taskService.complete(task.getId(), variables);
		}
		System.out.println("张三拒绝了任务");
		/*
		 * //部署流程 ProcessInstance pi =
		 * processEngine.getRuntimeService().startProcessInstanceByKey("myProcess");
		 * 
		 * TaskService taskService = processEngine.getTaskService();
		 * 
		 * List<Task> tasks =
		 * taskService.createTaskQuery().taskAssignee("zhangsan").list();
		 * 
		 * System.out.println( "zhangsan的任务数量 = " + tasks.size() );
		 * 
		 * for ( Task task : tasks ) { taskService.setVariable(task.getId(), "flag",
		 * false); taskService.complete(task.getId()); }
		 * 
		 * System.out.println( "流程结束" );
		 */

	}

	@Test
	public void sendMail() {
		repositoryService.createDeployment().addClasspathResource("email.bpmn").deploy();

		// 获取流程定义ID
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey("myProcess")
				.latestVersion().singleResult();
		// 启动
		ProcessInstance pi = runtimeService.startProcessInstanceById(pd.getId());
	}

}
