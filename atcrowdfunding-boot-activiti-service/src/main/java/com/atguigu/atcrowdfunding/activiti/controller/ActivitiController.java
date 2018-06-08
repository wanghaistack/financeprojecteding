package com.atguigu.atcrowdfunding.activiti.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.controller.BaseController;
import com.atguigu.atcrowdfunding.service.MemberService;
import com.atguigu.atcrowdfunding.util.Const;

@RestController
public class ActivitiController extends BaseController {
	@Autowired
	private MemberService memberService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;

	@RequestMapping("/nextPstepEnd")
	public void nextPstepEnd(@RequestBody Map<String, Object> variables) {
		String piid=(String) variables.get("piid");
		variables.get(Const.FLAG);
		Task task = taskService.createTaskQuery().processInstanceId(piid).singleResult();
		taskService.complete(task.getId(), variables);
	
	}
	@RequestMapping("/queryProcessTaskByTaskId/{taskid}")
	public Map<String, Object> queryProcessTaskByTaskId(@PathVariable("taskid") String taskid) {
		
		Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("taskName", task.getName());
		String processDefinitionId = task.getProcessDefinitionId();

		ProcessDefinition pd = 
				repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId)
				.singleResult();
		map.put("pdVersion", pd.getVersion());
		map.put("pdName", pd.getName());
		return map;

	}

	@RequestMapping("/queryPage")
	public List<Map<String, Object>> queryPage(@RequestBody Map<String, Object> paramMap) {
		TaskQuery taskQuery = taskService.createTaskQuery().taskCandidateGroup("backcheck");
		Integer startIndex = (Integer) paramMap.get("startIndex");
		Integer pagesize = (Integer) paramMap.get("pagesize");
		List<Task> list = taskQuery.listPage(startIndex, pagesize);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (Task task : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taskId", task.getId());
			map.put("taskName", task.getName());
			String piid = task.getProcessInstanceId();
			Member member = memberService.queryMember(piid);
			Ticket ticket = memberService.queryTicketByMemberId(member.getId());
			map.put("memberId", member.getId());
			map.put("realname", member.getRealname());
			// task------->pd流程
			String processDefinitionId = task.getProcessDefinitionId();
			ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(processDefinitionId).singleResult();
			String pdName = pd.getName();
			map.put("pdName", pdName);
			int version = pd.getVersion();
			map.put("version", version);
			listMap.add(map);
		}
		return listMap;
	}

	@RequestMapping("/queryPageCount")
	public int queryPageCount() {
		return (int) taskService.createTaskQuery().taskCandidateGroup("backcheck").count();
	}

	/*
	 * @RequestMapping("/taskComplet") public void taskComplet(@RequestBody
	 * Map<String, Object> variables) { String loginacct=(String)
	 * variables.get(Const.LOGINACCT); String piid =(String)
	 * variables.get(Const.PIID); //查询某人的流程任务 //根据流程实例id查询任务 TaskQuery taskQuery =
	 * taskService.createTaskQuery().processInstanceId(piid); //查询用户的任务 Task task =
	 * taskQuery.taskAssignee(loginacct).singleResult(); //完成用户的任务
	 * taskService.complete(task.getId(),variables); }
	 */
	@RequestMapping("/nextPstep")
	public void nextPstep(@RequestBody Map<String, Object> variables) {
		// 获取代理人信息
		String loginacct = (String) variables.get("loginacct");
		String piid = (String) variables.get("piid");
		TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(piid);
		Task task = taskQuery.taskAssignee(loginacct).singleResult();
		taskService.complete(task.getId(), variables);
	}

	@RequestMapping("/startProcess/{loginacct}")
	public String startProcess(@PathVariable("loginacct") String loginacct) {
		// 流程部署
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("authflow").latestVersion().singleResult();
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("loginacct", loginacct);
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
		return processInstance.getId();

	}

	@RequestMapping("/act/deleteProcess/{pdDeploymentId}")
	public void deleteProcess(@PathVariable("pdDeploymentId") String pdDeploymentId) {

		// 因为关于多张表外键关联，所以选用级联删除
		repositoryService.deleteDeployment(pdDeploymentId, true);// true代表用级联删除，false代表不用级联删除

	}

	@RequestMapping("/act/loadImgById/{pdid}")
	public byte[] loadImgById(@PathVariable("pdid") String pdid) {
		// 根据流程定义id查询流程定义
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition singleResult = processDefinitionQuery.processDefinitionId(pdid).singleResult();
		// 根据流程部署id和资源名称查询文件
		String deploymentId = singleResult.getDeploymentId();
		String diagramResourceName = singleResult.getDiagramResourceName();
		// 获取文件所对应的输入流
		InputStream in = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);
		// 准备内存流用于写数据
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();// 内存流
		byte[] buff = new byte[100];
		int rc = 0;
		try {
			while ((rc = in.read(buff, 0, 100)) > 0) {
				swapStream.write(buff, 0, rc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 将内存数据转换为字节数组
		byte[] in_b = swapStream.toByteArray();
		return in_b;

	}

	@RequestMapping("/act/deploy")
	public Object depolyProDef(@RequestParam("pdfile") MultipartFile file) {
		start();
		try {
			// 部署流程定义图形
			DeploymentBuilder deployment = repositoryService.createDeployment();
			deployment.addInputStream(file.getOriginalFilename(), file.getInputStream()).deploy();
			success(true);
		} catch (Exception e) {
			e.printStackTrace();
			success(false);
			message(e.getMessage());
		}
		return end();

	}

	@RequestMapping("act/countProcess")
	public Integer queryCount(@RequestBody Map<String, Object> paramMap) {
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		int count = (int) processDefinitionQuery.count();
		return count;
	}

	/*
	 * 传递参数： 简单的参数采用路径的形式传递 防止自关联，死循环，需要做集合转换
	 */
	/*
	 * 简单参数传递可以采用路径的形式 path/{参数}/{参数}...
	 * 复杂的参数可以使用map集合对象，或者vo(valueObject)对象,使用@RequestBody请求体注解传递
	 */
	@RequestMapping("/act/queryProcessDefinitionList")
	public List<Map<String, Object>> queryProcessDefinitionList(@RequestBody Map<String, Object> paramMap) {

		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		// 查询的对象如果存在内部自关联，那么在转换JSON时，会出现映射异常。
		// 需要做集合转换,否则,转换为json时报死循环.
		Integer startIndex = (Integer) paramMap.get("startIndex");
		Integer pagesize = (Integer) paramMap.get("pagesize");
		List<ProcessDefinition> listPage = processDefinitionQuery.listPage(startIndex, pagesize);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (ProcessDefinition pd : listPage) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pdId", pd.getId());
			map.put("pdKey", pd.getKey());
			map.put("pdName", pd.getName());
			map.put("pdVersion", pd.getVersion());
			map.put("pdDeploymentId", pd.getDeploymentId());
			list.add(map);

		}
		return list;

	}
	/*
	 * // @RequestMapping("/act/queryProcessDefinitionList/{pageno}/{pagesize}") //
	 * public List<Map<String, Object>> queryProcessDefinitionList(
	 * // @PathVariable("pageno") Integer pageno,
	 * // @PathVariable("pagesize")Integer pagesize) { // // ProcessDefinitionQuery
	 * processDefinitionQuery = repositoryService.createProcessDefinitionQuery(); //
	 * // 查询的对象如果存在内部自关联，那么在转换JSON时，会出现映射异常。 // //需要做集合转换,否则,转换为json时报死循环. // int
	 * startIndex=(pageno-1)*pagesize; // List<ProcessDefinition> listPage =
	 * processDefinitionQuery.listPage(startIndex, pagesize); // List<Map<String,
	 * Object>> list=new ArrayList<Map<String,Object>>(); // for (ProcessDefinition
	 * pd : listPage) { // Map<String, Object> map=new HashMap<String, Object>(); //
	 * map.put("pdId", pd.getId()); // map.put("pdKey", pd.getKey()); //
	 * map.put("pdName", pd.getName()); // map.put("pdVersion", pd.getVersion()); //
	 * map.put("pdDeploymentId", pd.getDeploymentId()); // list.add(map); // // } //
	 * return list; // // }
	 */
}
