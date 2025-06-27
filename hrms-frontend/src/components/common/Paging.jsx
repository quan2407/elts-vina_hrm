import { Pagination } from "antd";

 const Paging = ({totalElements, pageNumber, pageSize,setPageNumber, setPageSize }) => {
   return(<div style={{ textAlign: 'center', marginTop: 16 }}>
       <Pagination
           current={pageNumber}
           pageSize={pageSize}
           total={totalElements}
           showSizeChanger
           showQuickJumper
           showTotal={(total) => `Total ${total} items`}
           pageSizeOptions={['1','5', '10', '20', '50', '100']}
           onChange={(page, size) => {
               setPageNumber(page);
               setPageSize(size);
           }}
       />

   </div>)
}

export default Paging;