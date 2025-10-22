package com.example.quanlicuahangthuoc.controller;

import com.example.quanlicuahangthuoc.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    // Giả định bạn cũng có OrderService nếu cần lấy thông tin Order chi tiết

    @Autowired
    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    /**
     * Phương thức hiển thị danh sách chi tiết của một đơn hàng cụ thể.
     * @param orderId ID của đơn hàng
     * @param model Dữ liệu cho View
     * @return Tên View để hiển thị danh sách
     */
    @GetMapping("/order/{orderId}")
    public String listOrderDetails(@PathVariable Integer orderId, Model model) {
        // Lấy danh sách chi tiết đơn hàng
        model.addAttribute("orderDetails", orderDetailService.getDetailsByOrderId(orderId));
        model.addAttribute("orderId", orderId);
        // Thêm các logic khác để lấy thông tin Order

        // Ví dụ: Trả về file detail_list.html trong thư mục order/
        return "order/detail_list";
    }


    /**
     * Endpoint Xóa chi tiết đơn hàng.
     * CHÚ Ý: Mặc dù dùng GET, bạn nên dùng POST trong sản phẩm thực tế để đảm bảo an toàn.
     * @param id ID của OrderDetail cần xóa
     * @param orderId ID của Order chứa chi tiết (để chuyển hướng về đúng trang)
     * @param redirectAttributes Dùng để truyền thông báo qua redirect
     * @return Chuỗi chuyển hướng (Redirect) về trang danh sách
     */
    @GetMapping("/delete/{id}/{orderId}")
    public String deleteOrderDetail(@PathVariable Integer id,
                                    @PathVariable Integer orderId,
                                    RedirectAttributes redirectAttributes) {

        boolean isDeleted = orderDetailService.deleteOrderDetail(id);

        if (isDeleted) {
            // Thêm thông báo thành công.
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chi tiết đơn hàng ID " + id + " thành công!");
        } else {
            // Thêm thông báo lỗi.
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chi tiết đơn hàng với ID " + id + " để xóa.");
        }

        // Chuyển hướng người dùng trở lại trang danh sách chi tiết đơn hàng.
        // Dữ liệu đã xóa sẽ KHÔNG hiển thị vì trang này sẽ load lại dữ liệu từ DB.
        return "redirect:/admin/order-details/order/" + orderId;
    }
}