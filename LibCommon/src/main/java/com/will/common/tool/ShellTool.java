package com.will.common.tool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author chengzengqiang
 * 调用命令行
 */
public class ShellTool {
	public static final String COMMAND_SU = "su";
	public static final String COMMAND_SH = "sh";
	public static final String COMMAND_EXIT = "exit\n";
	public static final String COMMAND_LINE_END = "\n";

	public static boolean checkRootPermission() {
		return execCommand("echo root", true, false).result == 0;
	}

	public static CommandResult execCommand(String command, boolean isRoot) {
		return execCommand(new String[] { command }, isRoot, true);
	}

	public static CommandResult execCommand(List<String> commands,boolean isRoot) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}),isRoot, true);
	}

	public static CommandResult execCommand(String[] commands, boolean isRoot) {
		return execCommand(commands, isRoot, true);
	}

	public static CommandResult execCommand(String command, boolean isRoot,boolean isNeedResultMsg) {
		return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
	}

	public static CommandResult execCommand(List<String> commands,boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}),isRoot, isNeedResultMsg);
	}

	/**
	 * 1、得到Process对象Runtime.getRuntime().exec() 2、通过Process对象的OutPutStream写入命令
	 * 3、通过Process对象的waitFor()得到执行结果
	 * 4、通过Process对象的InputStream,ErrorStream读取返回结果的内容
	 * 
	 * @param commands
	 * @param isRoot
	 * @param isNeedResultMsg
	 * @return
	 * 
	 * 
	 */

	public static CommandResult execCommand(String[] commands, boolean isRoot,boolean isNeedResultMsg) {
		int result = -1;
		if (commands == null || commands.length == 0) {
			return new CommandResult(result, null, null);
		}

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;
		StringBuffer cmdBuffer = new StringBuffer();

		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
			os = new DataOutputStream(process.getOutputStream());
			for (String command : commands) {
				if (command == null) {
					continue;
				}
				cmdBuffer.append(command);
				cmdBuffer.append("  ");
				// donnot use os.writeBytes(commmand), avoid chinese charset
				// error
				os.write(command.getBytes());
				os.writeBytes(COMMAND_LINE_END);
				os.flush();
			}
			// cmdBuffer.append(COMMAND_EXIT);
			os.writeBytes(COMMAND_EXIT);
			os.flush();

			String cmdStr = new String(cmdBuffer);
			System.out.println("qiang cmd string is " + cmdStr);
			result = process.waitFor();
			// get command result
			if (isNeedResultMsg) {
				successMsg = new StringBuilder();
				errorMsg = new StringBuilder();
				successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
				errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String s;
				while ((s = successResult.readLine()) != null) {
					successMsg.append(s);
				}
				while ((s = errorResult.readLine()) != null) {
					errorMsg.append(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}
		System.out.println("qiang success "+ (successMsg == null ? null : successMsg.toString()));
		System.out.println("qiang error "+ (errorMsg == null ? null : errorMsg.toString()));
		return new CommandResult(result, successMsg == null ? null: successMsg.toString(), errorMsg == null ? null: errorMsg.toString());
	}

	/**
	 * result of command
	 * <ul>
	 * <li>{@link CommandResult#result} means result of command, 0 means normal,
	 * else means error, same to excute in linux shell</li>
	 * <li>{@link CommandResult#successMsg} means success message of command
	 * result</li>
	 * <li>{@link CommandResult#errorMsg} means error message of command result</li>
	 * </ul>
	 * 
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-5-16
	 */
	public static class CommandResult {

		/** result of command **/
		public int result;
		/** success message of command result **/
		public String successMsg;
		/** error message of command result **/
		public String errorMsg;

		public CommandResult(int result) {
			this.result = result;
		}

		public CommandResult(int result, String successMsg, String errorMsg) {
			this.result = result;
			this.successMsg = successMsg;
			this.errorMsg = errorMsg;
		}
	}

}
