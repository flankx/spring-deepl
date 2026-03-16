package com.github.springfinance;

import com.github.springfinance.SpringFinance;
import com.github.springfinance.model.Category;
import com.github.springfinance.model.Expense;
import com.github.springfinance.model.Income;
import com.github.springfinance.service.CategoryService;
import com.github.springfinance.service.ExpenseService;
import com.github.springfinance.service.IncomeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringFinance.class)
public class FinanceTests {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private IncomeService incomeService;
    
    @Test
    public void testCategoryCRUD() {
        // 创建分类
        Category category = new Category();
        category.setName("测试分类");
        category.setType("EXPENSE");
        category.setDescription("测试分类描述");
        Category savedCategory = categoryService.save(category);
        assertNotNull(savedCategory.getId());
        
        // 查询分类
        Category foundCategory = categoryService.findById(savedCategory.getId());
        assertNotNull(foundCategory);
        assertEquals("测试分类", foundCategory.getName());
        
        // 更新分类
        foundCategory.setName("更新后的分类");
        Category updatedCategory = categoryService.save(foundCategory);
        assertEquals("更新后的分类", updatedCategory.getName());
        
        // 删除分类
        categoryService.deleteById(updatedCategory.getId());
        assertNull(categoryService.findById(updatedCategory.getId()));
    }
    
    @Test
    public void testExpenseCRUD() {
        // 创建分类
        Category category = new Category();
        category.setName("餐饮");
        category.setType("EXPENSE");
        category = categoryService.save(category);
        
        // 创建支出
        Expense expense = new Expense();
        expense.setAmount(100.0);
        expense.setDate(LocalDate.now());
        expense.setDescription("午餐");
        expense.setCategory(category);
        Expense savedExpense = expenseService.save(expense);
        assertNotNull(savedExpense.getId());
        
        // 查询支出
        Expense foundExpense = expenseService.findById(savedExpense.getId());
        assertNotNull(foundExpense);
        assertEquals(100.0, foundExpense.getAmount());
        
        // 更新支出
        foundExpense.setAmount(150.0);
        Expense updatedExpense = expenseService.save(foundExpense);
        assertEquals(150.0, updatedExpense.getAmount());
        
        // 删除支出
        expenseService.deleteById(updatedExpense.getId());
        assertNull(expenseService.findById(updatedExpense.getId()));
        
        // 删除分类
        categoryService.deleteById(category.getId());
    }
    
    @Test
    public void testIncomeCRUD() {
        // 创建分类
        Category category = new Category();
        category.setName("工资");
        category.setType("INCOME");
        category = categoryService.save(category);
        
        // 创建收入
        Income income = new Income();
        income.setAmount(5000.0);
        income.setDate(LocalDate.now());
        income.setDescription("月工资");
        income.setCategory(category);
        Income savedIncome = incomeService.save(income);
        assertNotNull(savedIncome.getId());
        
        // 查询收入
        Income foundIncome = incomeService.findById(savedIncome.getId());
        assertNotNull(foundIncome);
        assertEquals(5000.0, foundIncome.getAmount());
        
        // 更新收入
        foundIncome.setAmount(6000.0);
        Income updatedIncome = incomeService.save(foundIncome);
        assertEquals(6000.0, updatedIncome.getAmount());
        
        // 删除收入
        incomeService.deleteById(updatedIncome.getId());
        assertNull(incomeService.findById(updatedIncome.getId()));
        
        // 删除分类
        categoryService.deleteById(category.getId());
    }
    
    @Test
    public void testExpenseAndIncomeTotals() {
        // 创建分类
        Category expenseCategory = new Category();
        expenseCategory.setName("购物");
        expenseCategory.setType("EXPENSE");
        expenseCategory = categoryService.save(expenseCategory);
        
        Category incomeCategory = new Category();
        incomeCategory.setName("奖金");
        incomeCategory.setType("INCOME");
        incomeCategory = categoryService.save(incomeCategory);
        
        // 创建支出和收入
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        
        for (int i = 0; i < 5; i++) {
            Expense expense = new Expense();
            expense.setAmount(100.0 * (i + 1));
            expense.setDate(startDate.plusDays(i));
            expense.setDescription("购物支出 " + (i + 1));
            expense.setCategory(expenseCategory);
            expenseService.save(expense);
        }
        
        for (int i = 0; i < 3; i++) {
            Income income = new Income();
            income.setAmount(1000.0 * (i + 1));
            income.setDate(startDate.plusDays(i));
            income.setDescription("奖金收入 " + (i + 1));
            income.setCategory(incomeCategory);
            incomeService.save(income);
        }
        
        // 测试支出总额
        Double totalExpense = expenseService.getTotalExpense(startDate, endDate);
        assertNotNull(totalExpense);
        assertEquals(1500.0, totalExpense); // 100+200+300+400+500
        
        // 测试收入总额
        Double totalIncome = incomeService.getTotalIncome(startDate, endDate);
        assertNotNull(totalIncome);
        assertEquals(6000.0, totalIncome); // 1000+2000+3000
        
        // 测试分类支出总额
        Double categoryExpense = expenseService.getTotalExpenseByCategory(expenseCategory.getId(), startDate, endDate);
        assertNotNull(categoryExpense);
        assertEquals(1500.0, categoryExpense);
        
        // 测试分类收入总额
        Double categoryIncome = incomeService.getTotalIncomeByCategory(incomeCategory.getId(), startDate, endDate);
        assertNotNull(categoryIncome);
        assertEquals(6000.0, categoryIncome);
        
        // 清理数据
        List<Expense> expenses = expenseService.findByCategoryId(expenseCategory.getId());
        expenses.forEach(expense -> expenseService.deleteById(expense.getId()));
        
        List<Income> incomes = incomeService.findByCategoryId(incomeCategory.getId());
        incomes.forEach(income -> incomeService.deleteById(income.getId()));
        
        categoryService.deleteById(expenseCategory.getId());
        categoryService.deleteById(incomeCategory.getId());
    }
    
}
