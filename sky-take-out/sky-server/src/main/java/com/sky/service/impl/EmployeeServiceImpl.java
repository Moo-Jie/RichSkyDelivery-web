package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.*;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.IdentityCardUtil;
import com.sky.utils.PhoneUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询密码
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）会被全局异常捕获到，程序报错停止运行
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传来的密码进行MD5加密处理，再与数据库中查询到的密码进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、没有报错，返回实体对象
        return employee;
    }

    @Override
    public void saveEmployee(EmployeeDTO employeeDTO) {
        //判断身份证号和手机号是否符合标准长度
        if (!IdentityCardUtil.validate(employeeDTO.getIdNumber())) {
            throw new PersonIDException(MessageConstant.ORDER_PersonID_ERROR);
        }

        if (!PhoneUtil.isMobileNumber(employeeDTO.getPhone())) {
            throw new PhoneException(MessageConstant.ORDER_Phone_ERROR);
        }

        //由于持久层的方法参数为Employee，而前端传来的是EmployeeDTO，所以需要进行转换
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置其他属性
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));//设置为初始密码，再进行MD5加密处理
        employee.setStatus(StatusConstant.ENABLE);//设置为启用状态
        //employee.setCreateTime(LocalDateTime.now());AOP内实现
        //employee.setUpdateTime(LocalDateTime.now());AOP内实现
        //设置操作用户ID
        // 获取当前登录用户的ID
        //employee.setCreateUser(BaseContext.getCurrentId());AOP内实现
        //employee.setUpdateUser(BaseContext.getCurrentId());AOP内实现
        //调用持久层方法
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult getEmployeePage(EmployeePageQueryDTO employeePageQueryDTO) {
        //设置分页参数
        //startPage底层把分页参数放入ThreadLocal中，mybatis在查询时会从ThreadLocal中获取分页参数，然后进行分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //执行查询(Page<>本身就继承于ArrayList<>容器，因此mybaties可以顺利返回结果)
        Page<Employee> page=employeeMapper.getEmployeeByName(employeePageQueryDTO.getName());
        //封装分页查询结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeMapper.getEmployeeById(id);
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        //判断身份证号和手机号是否符合标准长度
        if (!IdentityCardUtil.validate(employeeDTO.getIdNumber())) {
            throw new PersonIDException(MessageConstant.ORDER_PersonID_ERROR);
        }

        if (!PhoneUtil.isMobileNumber(employeeDTO.getPhone())) {
            throw new PhoneException(MessageConstant.ORDER_Phone_ERROR);
        }
        //由于持久层的方法参数为Employee，而前端传来的是EmployeeDTO，所以需要进行转换
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置其他属性
        //employee.setUpdateTime(LocalDateTime.now());AOP内实现
        //设置操作用户ID
        //employee.setUpdateUser(BaseContext.getCurrentId());AOP内实现
        System.out.println(employee);
        //调用持久层方法
        employeeMapper.update(employee);
    }

    @Override
    public void startOrStopStatus(Integer status, Long id) {
        Employee emp = Employee.builder()
                .id(id)
                .status(status)
                .build();
        //调用持久层方法
        employeeMapper.update(emp);
    }
}
